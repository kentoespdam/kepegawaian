# Documentation Update: Laporan Mapping & Exception Refactoring

## Changes Overview

1. **Laporan Mapping & DNP Response**:
   - Added `orgGroup` field support to `DnpResponse` and `DnpRecordMapper`.
   - Updated report record mappers (`DukRecordMapper`, `KenaikanBerkalaRecordMapper`, `KontrakRecordMapper`, `LtaRecordMapper`, `MutasiRecordMapper`, `SoRecordMapper`) and `DnpRepository`.
   - Updated unit tests (`DnpRecordMapperTest`, `DnpServiceTest`).

2. **Static Analysis & Exception Handling Refactoring**:
   - Resolved PMD warning `AvoidThrowingRawExceptionTypes` by replacing raw `RuntimeException` with `IllegalStateException` across report services and utilities (`DnpService`, `DukService`, `KenaikanBerkalaService`, `KontrakService`, `LtaService`, `MutasiService`, `StatistikService`, `FileUploadUtilImpl`, `ProcessPotonganTkkImpl`, `GajiBatchProsesCommandService`).
   - Resolved PMD warning `AvoidCatchingGenericException` by replacing generic `catch (Exception e)` with specific `catch (IOException e)` in report export services.
