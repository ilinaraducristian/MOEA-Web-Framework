package org.moeawebframework.moeawebframework.exceptions

const val AlgorithmNotFoundOrAccessDeniedException = "The algorithm doesn't exist or user doesn't have access to it"
const val ProblemNotFoundOrAccessDeniedException = "The problem doesn't exist or user doesn't have access to it"
const val ReferenceSetNotFoundOrAccessDeniedException = "The reference set doesn't exist or user doesn't have access to it"

const val InternalServerException = "Internal server error"

const val AlreadyProcessingException = "Already processing"
const val AlreadyProcessedException = "Already processed"
const val ProcessNotFoundException = "Process not found"
const val QueueItemNotFoundException = "The queue item does not exist or it doesn't belong to this user"