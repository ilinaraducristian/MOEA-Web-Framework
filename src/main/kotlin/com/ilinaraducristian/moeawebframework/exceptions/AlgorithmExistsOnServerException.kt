package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class AlgorithmExistsOnServerException: RuntimeException("Algorithm file exists on server, use override to replace it")