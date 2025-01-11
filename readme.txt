Assumptions:
    -cannot have duplicate ID's (seems fairly obvious but as it is an input and validated against this is an assumption)
    -Position is always going to be a long (Cannot have a portion value of a security)
    -Object sent in is a type of JSON (as opposed to a string of the data)
    -the end user will only ever want to retrieve 1 security
    -selling more than is available will return a position of 0
    -If you request a position that doesn't exist the request will return a position value of 0

TODO:
    -add JavaDoc
    -add validator
    -add final tests for validator
    -push to GitHub and send off