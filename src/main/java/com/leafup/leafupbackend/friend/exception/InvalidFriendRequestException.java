package com.leafup.leafupbackend.friend.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class InvalidFriendRequestException extends InvalidGroupException {
    public InvalidFriendRequestException(String message) {
        super(message);
    }
}
