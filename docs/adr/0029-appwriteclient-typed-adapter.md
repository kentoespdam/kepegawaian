# Ekstraksi REST client Appwrite ke typed adapter `AppwriteClient`

> **Status:** accepted — hasil implementasi dari sesi improve-codebase-architecture (grilling 2026-07-08).

## Konteks

`AuthService` dan `JwtTokenService` sebelumnya memanggil REST API Appwrite secara langsung melalui `RestClient` dengan duplikasi header (X-Appwrite-Project, X-Appwrite-Key, X-Appwrite-Response-Format, Content-Type) dan URL concatenation manual (`endpoint + "/users/..."`) di setiap method. Total ada 6 titik panggil REST yang identik konstruksi HTTP-nya (5 di AuthService, 1 di JwtTokenService), masing-masing dengan 3 `@Value` field yang sama untuk konfigurasi endpoint/projectId/apiKey.

Selain duplikasi, terdapat practice bermasalah: `System.out.println(response)` di `createUser`, dan exception swallowing di `updatePref` (`catch (Exception e) { log.info(...) }`) yang mengembalikan SUCCESS meskipun panggilan API gagal.

## Keputusan

Buat **`AppwriteClient`** — kelas `@Component` konkret, bukan interface (sesuai ADR-0007) — yang meng-enkapsulasi semua komunikasi HTTP ke Appwrite:

```java
@Component
@RequiredArgsConstructor
public class AppwriteClient {
    private final RestClient restClient;
    private final AppwriteProperties properties;

    public AppwriteUser getUser(String id) { ... }
    public String createUser(AppwriteUserPostRequest request) { ... }
    public AppwriteUser updateStatus(String id, UserPatchStatusRequest status) { ... }
    public void updatePrefs(String id, List<PrefRole> prefRoles) { ... }
    public void createUserWithDefaultRoles(String userId, String email, String password, String name) { ... }
    public AppwriteUser validateToken(String token) { ... }
}
```

Bersama dengan **`AppwriteProperties`** — `@ConfigurationProperties(prefix = "appwrite")` — untuk menggantikan 3 `@Value` yang tersebar.

Dua service consumer di-refactor:
- **`AuthService`** — inject `AppwriteClient`, hapus `RestClient` + 3 `@Value`. `System.out.println` diganti `log.debug`.
- **`JwtTokenService`** — inject `AppwriteClient` untuk `validateToken()`. Class dipertahankan untuk backward compatibility dengan `JwtAuthFilter`.

## Considered Options

- **Biarkan duplikasi** (ditolak): 6 titik panggil REST identik dengan header construction duplikat. Melanggar DRY dan membuat testing sulit (perlu mock RestClient di setiap service).
- **Hanya `AppwriteProperties` tanpa adapter** (ditolak): mengurangi duplikasi konfigurasi (`@Value` → bean typed), tetapi header construction dan URL concatenation tetap terduplikasi.
- **Extract ke adapter penuh + merge JwtTokenService** (ditolak untuk saat ini — Opsi B): menghapus `JwtTokenService` dan meng-update `JwtAuthFilter` untuk inject `AppwriteClient` langsung. Risiko perubahan di security filter chain tidak sebanding dengan benefit untuk scope ini. Ditunda sebagai follow-up.

## Consequences

- **Header construction terpusat** di `AppwriteClient.addDefaultHeaders()` — 4 header × 6 titik = 24 baris boilerplate dihilangkan.
- **Konfigurasi Appwrite terpusat** di `AppwriteProperties` — tidak ada lagi 3 `@Value` di 2 file terpisah.
- **Testing** — `AppwriteClient` bisa di-mock; unit test AuthService/JwtTokenService tidak perlu mock `RestClient`.
- **`JwtTokenService` tetap ada** — `JwtAuthFilter` tidak berubah. Layering tipis (1 method delegasi) yang bisa di-merge nanti tanpa mengubah filter chain.
- **Perilaku `updatePrefs` exception swallowing dipertahankan** — `AuthService.updatePref()` tetap mengembalikan SUCCESS walau panggilan API gagal. Ini adalah behavior yang dipertahankan secara sadar (parity), bukan diperbaiki dalam refactor ini. Bila ingin diperbaiki, perlu beads issue terpisah.
- **`System.out.println` dihapus** — diganti `log.debug` di `AppwriteClient.createUser()`.
