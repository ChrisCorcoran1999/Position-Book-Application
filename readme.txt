Assumptions:
    -Cannot create events which have duplicate ID's (seems fairly obvious but as it is an input and validated against this is an assumption)
    -Position is always going to be a long (Cannot have a portion value position)
    -Object sent in is a type of JSON (as opposed to a string of the data)
    -The end user will only ever want to retrieve one position from a security account matching
    -Selling more than the current position will return a position of 0
    -If you request a position that doesn't exist the request will return a position value of 0
    -You cannot buy negative position
