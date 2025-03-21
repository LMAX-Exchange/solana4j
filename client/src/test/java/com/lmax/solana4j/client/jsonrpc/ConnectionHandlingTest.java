package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.SolanaClient;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
final class ConnectionHandlingTest
{
    public static final String DUMMY_API_URL = "http://solana.test.org:8899";
    public static final String DUMMY_JSONRPC_RESPONSE = "{\"jsonrpc\": \"2.0\", \"id\": 1, \"result\": {\"dummy\": \"value\"}}";
    public static final String DUMMY_ADDRESS = "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri";

    @Mock
    HttpClient httpClient;

    @Mock
    HttpResponse httpResponse;

    @Captor
    ArgumentCaptor<HttpRequest> requestCaptor;

    @BeforeEach
    void setUp() throws IOException, InterruptedException
    {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(httpResponse);
    }

    @Test
    void shouldCallCorrectUrl() throws SolanaJsonRpcClientException, IOException, InterruptedException
    {
        final SolanaApi solanaApi = SolanaClient.create(httpClient, DUMMY_API_URL);
        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.body()).thenReturn(DUMMY_JSONRPC_RESPONSE);

        final SolanaClientResponse<AccountInfo> response = solanaApi.getAccountInfo("abc123");

        assertThat(response.isSuccess()).isTrue()
                .describedAs("The response was not a success");
        Mockito.verify(httpClient).send(requestCaptor.capture(), Mockito.any());
        assertThat(requestCaptor.getValue().uri().toString()).isEqualTo(DUMMY_API_URL)
                .describedAs("The URL was incorrect");
    }

    @Test
    void shouldHandleJsonrpcError() throws SolanaJsonRpcClientException
    {
        final SolanaApi solanaApi = SolanaClient.create(httpClient, DUMMY_API_URL);
        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.body()).thenReturn(
                "{\"jsonrpc\": \"2.0\", \"id\": 1, \"error\": {" +
                        "\"code\": \"9\", \"message\": \"Oh no!\", \"data\": \"123456\"}}");

        final SolanaClientResponse<AccountInfo> response = solanaApi.getAccountInfo("abc123");

        assertThat(response.isSuccess()).isFalse()
                .describedAs("The response was not a success");
        assertThat(response.getError().getErrorCode()).isEqualTo(9)
                .describedAs("The error code was wrong");
        assertThat(response.getError().getErrorMessage()).isEqualTo("Oh no!")
                .describedAs("The error message was wrong");
    }

    @Test
    void shouldHandleHttpErrorCode()
    {
        final SolanaApi solanaApi = SolanaClient.create(httpClient, DUMMY_API_URL);
        Mockito.when(httpResponse.statusCode()).thenReturn(500);

        assertThrows(SolanaJsonRpcClientException.class, () ->
            solanaApi.getAccountInfo(DUMMY_ADDRESS)
        );
    }

    @Test
    void shouldSetSocketTimeoutIfSpecified() throws SolanaJsonRpcClientException, IOException, InterruptedException
    {
        final Duration socketTimeout = Duration.of(10, ChronoUnit.SECONDS);
        final SolanaApi solanaApi = SolanaClient.create(httpClient, DUMMY_API_URL, socketTimeout);
        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.body()).thenReturn(DUMMY_JSONRPC_RESPONSE);

        solanaApi.getAccountInfo(DUMMY_ADDRESS);

        Mockito.verify(httpClient).send(requestCaptor.capture(), Mockito.any());
        assertThat(requestCaptor.getValue().timeout().isPresent()).isTrue()
                .describedAs("The timeout should have been set");
        assertThat(requestCaptor.getValue().timeout().get()).isEqualTo(socketTimeout)
                .describedAs("The timeout was incorrect");
    }

    @Test
    void shouldNotSetSocketTimeoutIfUnspecified() throws SolanaJsonRpcClientException, IOException, InterruptedException
    {
        final SolanaApi solanaApi = SolanaClient.create(httpClient, DUMMY_API_URL);
        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.body()).thenReturn(DUMMY_JSONRPC_RESPONSE);

        solanaApi.getAccountInfo(DUMMY_ADDRESS);

        Mockito.verify(httpClient).send(requestCaptor.capture(), Mockito.any());
        assertThat(requestCaptor.getValue().timeout().isEmpty()).isTrue()
                .describedAs("The timeout should not have been set");
    }
}
