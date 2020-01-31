package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class ProblemExistsOnServerException: RuntimeException("Problem file exists on server, use override to replace it")