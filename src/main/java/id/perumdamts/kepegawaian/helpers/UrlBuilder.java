package id.perumdamts.kepegawaian.helpers;

public class UrlBuilder {
    public static String build(String BasePath, String endpoint) {
        StringBuilder urlBuilder = new StringBuilder(BasePath);

        return endpoint.isEmpty() ? urlBuilder.toString() : urlBuilder.append(endpoint).toString();
    }

    public static String build(String BasePath, String endpoint, Object request) {
        StringBuilder urlBuilder = new StringBuilder(BasePath);
        if (endpoint.isEmpty())
            return urlBuilder.append(request.toString()).toString();
        return urlBuilder.append(endpoint).append(request.toString()).toString();
    }

    public static String buildFilter(String BasePath, String endpoint, Enum<?> request) {
        StringBuilder urlBuilder = new StringBuilder(BasePath);
        StringBuilder filterBuilder = new StringBuilder();

        if (request != null)
            filterBuilder.append("?filter=").append(request);

        if (endpoint.isEmpty())
            return urlBuilder.append(filterBuilder).toString();
        return urlBuilder.append(endpoint).append(filterBuilder).toString();
    }
}
