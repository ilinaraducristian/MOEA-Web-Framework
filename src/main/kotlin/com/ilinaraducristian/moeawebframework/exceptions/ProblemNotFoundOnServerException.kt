package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class ProblemNotFoundOnServerException: RuntimeException("Problem file doesn't not exist on server")