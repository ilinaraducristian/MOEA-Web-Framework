package org.moeawebframework.exceptions

const val ProblemNotFoundOrAccessDeniedException = "The problem doesn't exist or user doesn't have access to it"
const val ReferenceSetNotFoundOrAccessDeniedException =
    "The reference set doesn't exist or user doesn't have access to it"

const val InternalServerException = "Internal server error"

const val AlreadyProcessingException = "Already processing"
const val AlreadyProcessedException = "Already processed"
const val ProcessNotFoundException = "Process not found"
const val QueueItemNotFoundException = "The queue item does not exist or it doesn't belong to this user"

const val AlgorithmCouldNotBeCreated = "Algorithm could not be created"
const val CommonStructureExists = "Common structure already exists"
const val CommonStructureExistsAccessDenied = "The algorithm doesn't exist or user doesn't have access to it"
const val CommonStructureDoesNotExist = "common structure does not exist"
const val AlgorithmExistsAccessDenied = "Algorithm exists but you don't have access to it"

const val EvaluationCouldNotBeCreated = "Evaluation could not be created"
const val CommonStructureCouldNotBeCreated = "Common structure could not be created"