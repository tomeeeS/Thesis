package tmit.bme.telkicar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if(statusCode == HttpStatus.NOT_FOUND.value()) {
				return "error/error-404";
			}

			if(statusCode == HttpStatus.BAD_GATEWAY.value()) {
				return "error/error-502";
			}

			if(statusCode == HttpStatus.BAD_REQUEST.value()) {
				return "error/error-400";
			}

			if(statusCode == HttpStatus.BANDWIDTH_LIMIT_EXCEEDED.value()) {
				return "error/error-509";
			}

			if(statusCode == HttpStatus.CONFLICT.value()) {
				return "error/error-409";
			}

			if(statusCode == HttpStatus.EXPECTATION_FAILED.value()) {
				return "error/error-417";
			}

			if(statusCode == HttpStatus.FAILED_DEPENDENCY.value()) {
				return "error/error-424";
			}

			if(statusCode == HttpStatus.FORBIDDEN.value()) {
				return "error/error-403";
			}

			if(statusCode == HttpStatus.GATEWAY_TIMEOUT.value()) {
				return "error/error-504";
			}

			if(statusCode == HttpStatus.GONE.value()) {
				return "error/error-410";
			}

			if(statusCode == HttpStatus.HTTP_VERSION_NOT_SUPPORTED.value()) {
				return "error/error-505";
			}

			if(statusCode == HttpStatus.I_AM_A_TEAPOT.value()) {
				return "error/error-418";
			}

			if(statusCode == HttpStatus.INSUFFICIENT_STORAGE.value()) {
				return "error/error-507";
			}

			if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "error/error-500";
			}

			if(statusCode == HttpStatus.LENGTH_REQUIRED.value()) {
				return "error/error-411";
			}

			if(statusCode == HttpStatus.LOCKED.value()) {
				return "error/error-423";
			}

			if(statusCode == HttpStatus.LOOP_DETECTED.value()) {
				return "error/error-508";
			}

			if(statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
				return "error/error-405";
			}

			if(statusCode == HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value()) {
				return "error/error-511";
			}

			if(statusCode == HttpStatus.NOT_ACCEPTABLE.value()) {
				return "error/error-406";
			}

			if(statusCode == HttpStatus.NOT_EXTENDED.value()) {
				return "error/error-510";
			}

			if(statusCode == HttpStatus.NOT_IMPLEMENTED.value()) {
				return "error/error-501";
			}

			if(statusCode == HttpStatus.PAYLOAD_TOO_LARGE.value()) {
				return "error/error-413";
			}

			if(statusCode == HttpStatus.PAYMENT_REQUIRED.value()) {
				return "error/error-402";
			}

			if(statusCode == HttpStatus.PRECONDITION_FAILED.value()) {
				return "error/error-412";
			}

			if(statusCode == HttpStatus.PRECONDITION_REQUIRED.value()) {
				return "error/error-428";
			}

			if(statusCode == HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value()) {
				return "error/error-407";
			}

			if(statusCode == HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.value()) {
				return "error/error-431";
			}

			if(statusCode == HttpStatus.REQUEST_TIMEOUT.value()) {
				return "error/error-408";
			}

			if(statusCode == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()) {
				return "error/error-416";
			}

			if(statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
				return "error/error-503";
			}

			if(statusCode == HttpStatus.TOO_EARLY.value()) {
				return "error/error-425";
			}

			if(statusCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
				return "error/error-429";
			}

			if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
				return "error/error-401";
			}

			if(statusCode == HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value()) {
				return "error/error-451";
			}

			if(statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
				return "error/error-422";
			}

			if(statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
				return "error/error-415";
			}

			if(statusCode == HttpStatus.UPGRADE_REQUIRED.value()) {
				return "error/error-426";
			}

			if(statusCode == HttpStatus.URI_TOO_LONG.value()) {
				return "error/error-414";
			}

			if(statusCode == HttpStatus.VARIANT_ALSO_NEGOTIATES.value()) {
				return "error/error-506";
			}

		}
		return "error/error";
	}
}
