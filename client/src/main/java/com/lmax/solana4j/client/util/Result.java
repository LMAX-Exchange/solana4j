package com.lmax.solana4j.client.util;

import java.util.function.Function;

public final class Result<L, R>
{
    private final L error;
    private final R success;

    private Result(final L error, final R success)
    {
        this.error = error;
        this.success = success;
    }

    public static <L, R> Result<L, R> error(final L error)
    {
        return new Result<>(error, null);
    }

    public static <L, R> Result<L, R> success(final R success)
    {
        return new Result<>(null, success);
    }

    public boolean isError()
    {
        return error != null;
    }

    public boolean isSuccess()
    {
        return success != null;
    }

    public L getError()
    {
        return error;
    }

    public R getSuccess()
    {
        return success;
    }

    public <S> Result<L, S> onSuccess(final Function<R, Result<L, S>> onSuccess)
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