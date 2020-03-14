package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class InvalidNumberOfEvaluationsException : RuntimeException("Number of evaluations is less than 500")