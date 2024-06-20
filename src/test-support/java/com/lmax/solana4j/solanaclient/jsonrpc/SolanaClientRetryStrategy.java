package com.lmax.solana4j.solanaclient.jsonrpc;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

class SolanaClientRetryStrategy implements ServiceUnavailableRetryStrategy
{
    static final int RATE_LIMITED_ERROR_CODE = 429;

    private final int maxRetries;
    private final long retryInterval;

    SolanaClientRetryStrategy(final int maxRetries, final long retryInterval)
    {
        this.maxRetries = maxRetries;
        this.retryInterval = retryInterval;
    }

    @Override
    public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context)
    {
        if (executionCount >= maxRetries)
        {
            return false;
        }
        final int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == RATE_LIMITED_ERROR_CODE || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE;
    }

    @Override
    public long getRetryInterval()
    {
        return retryInterval;
    }
}
