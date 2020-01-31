package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class UserNotLoggedInException : RuntimeException("Please log in to use this feature")