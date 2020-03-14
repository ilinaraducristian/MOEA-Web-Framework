package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class InvalidNumberOfSeedsException : RuntimeException("Number of seeds is less than 1")