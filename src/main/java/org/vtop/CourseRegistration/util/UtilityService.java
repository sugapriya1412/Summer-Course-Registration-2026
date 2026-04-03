package org.vtop.CourseRegistration.util;

import java.util.Collections;
import java.util.List;

public class UtilityService {

        public static final int EMPLOYEE_NOT_FOUND = -1;
        public static final int APP_NOT_INSTALLED = -2;
        public static final int PARAMETER_EMPLOYEE_ID_NULL = -3;

        public static String processResult(int responseCode) {
                String message = "";

                if (responseCode >= 200 && responseCode < 300) {
                        message = " Success";
                } else if (responseCode >= 300 && responseCode < 400) {
                        message = " Resource Not Found";
                } else if (responseCode >= 400 && responseCode < 500) {
                        message = " Bad Request";
                } else if (responseCode >= 500 && responseCode < 600) {
                        message = " Internal Server Error";
                } else if (responseCode == EMPLOYEE_NOT_FOUND) {
                        message = " Employee Id Not found";
                } else if (responseCode == APP_NOT_INSTALLED) {
                        message = " App Not Installed ";
                }

                return message;

        }

        public static boolean isSuccess(int responseCode) {
                return (responseCode >= 200 && responseCode < 300);
        }

        public static <T> List<T> nullSafe(List<T> other) {
                return other == null ? Collections.<T>emptyList() : other;
        }
}
