package com.redmondsims.gistfx.enums;

public enum LoginStates {
	WRONG_PASSWORD_ENTERED,
	NEED_TOKEN,
	NEED_PASSWORD,
	NO_LOCAL_CREDS,
	ILLEGAL_PASSWORD,
	HASHING_NEW_PASSWORD,
	HAS_LOCAL_CREDS,
	TOKEN_FAILURE,
	AMBIGUOUS,
	BUILD_TOKEN_ONLY,
	BUILD_PASSWORD_ONLY,
	BUILD_BOTH,
	USER_CANCELED_CONFIRM_PASSWORD,
	ALL_CREDS_VALID,
	PASSWORD_MISMATCH,
	TOO_MANY_PASSWORD_ATTEMPTS,
	TOKEN_VALID,
	SAVED_CHECKED,
	SAVED_UNCHECKED,
	NO_CREDS_GIVEN
}