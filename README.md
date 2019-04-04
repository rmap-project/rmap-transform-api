# rmap-transform-api
Web api for triggering an ETL routine to convert a record from a 3rd party API e.g. OSF to an RMap DiSCO

This was based on the now deprecated https://github.com/rmap-project/rmap-transformer but it may be useful to integrate with the https://github.com/rmap-project/rmap-loader-osf project. 

The idea stemmed from work with OSF when we were thinking about how a user could initiate creating or updating a DiSCO for their OSF project or user record.  The idea was to have a web api that you could access using your RMap API key, in order to initiate an ETL process for a specific record on a specific API.  An API call might be formatted like this, for example:

```
POST /transforms/{type}?id={api_lookup_id}[&discoid={orig_disco_id}]
# Examples:
# to transform a user
POST /tranforms/osf_user?id=abcde
# to update a DiSCO from an OSF node - in this instance the duplicate checker in the loader could confirm that the DiSCO has changed.
POST /tranforms/osf_node?id=fghij&discoid=rmap:ajfskj23jf
```

There are some serious problems with the code as it stands:
1. it doesn't build 
2. the tests are pointing at a dev server that no longer exists
3. it is using an old version of the osf harvester that probably no longer works - the new https://github.com/rmap-project/rmap-loader-osf would need to be integrated

As a general concept and for the API work already done here, it may be useful to look at in the future.
