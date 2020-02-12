package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class AlgorithmNotFoundOnServerException: RuntimeException("Algorithm file doesn't not exist on server")