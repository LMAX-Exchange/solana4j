package com.lmax.solana4j.client.jsonrpc;

import java.util.function.Function;

final class Result<L, R>
{
    private final L error;
    private final R success;

    private Result(final L error, final R success)
    {
        this.error = error;
        this.success = success;
    }

    static <L, R> Result<L, R> error(final L error)
    {
        return new Result<>(error, null);
    }

    static <L, R> Result<L, R> success(final R success)
    {
        return new Result<>(null, success);
    }

    boolean isError()
    {
        return error != null;
    }

    boolean isSuccess()
    {
        return success != null;
    }

    L getError()
    {
        return error;
    }

    R getSuccess()
    {
        return success;
    }

    <S> Result<L, S> onSuccess(final Function<R, Result<L, S>> onSuccess)
    {
        if (isSuccess())
        {
            final var intermediateResult = onSuccess.apply(success);

            if (intermediateResult.isError())
            {
                return Result.error(intermediateResult.getError());
            }
            else
            {
                return Result.success(intermediateResult.success);
            }
        }
        else
        {
            return Result.error(error);
        }
    }
}