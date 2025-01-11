Assumptions:
    -Cannot create events which have duplicate ID's (seems fairly obvious but as it is an input and validated against this is an assumption)
    -Position is always going to be a long (Cannot have a portion value position)
    -Object sent in is a type of JSON (as opposed to a string of the data)
    -The end user will only ever want to retrieve one position from a security account matching
    -Selling more than the current position will return a position of 0 but will still be used when calculating total position (ie as if it has been pre sold)
    -If you request a position that doesn't exist the request will return a position value of 0
    -You cannot buy negative position
    -Previously added positions should be returned in next rest call (ie the position returned is the sum of all trades not just the ones from the most recent)
    -Only the positions of the accounts referenced in the input will be returned in the output (retrieving data of the other inputs is done via the get end point)