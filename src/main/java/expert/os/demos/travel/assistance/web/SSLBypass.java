package expert.os.demos.travel.assistance.web;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public final class SSLBypass {

    public static void disableSslVerification() {

        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(
                                X509Certificate[] chain,
                                String authType) {
                        }

                        @Override
                        public void checkServerTrusted(
                                X509Certificate[] chain,
                                String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(
                    null,
                    trustAllCerts,
                    new SecureRandom()
            );

            SSLContext.setDefault(sslContext);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLBypass() {
    }
}