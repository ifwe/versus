##User
`POST /users/login`


Body fields:

name    |type   |possible values
--------|-------|---------------
fbid    |string |               
token   |string |       
name    |string |             
~~email~~|~~string~~|             

Response:

```
{
  "name" : "HERP DERP",
  "fbid" : "10156715917220243",
  "energy" : 5,
  "last_energy_update" : "1460588831566",
  "next_energy_update" : "2342342342342"
}
```

`GET /users/getUser`


Query parameters:

name    |type   |possible values
--------|-------|---------------
fbid    |string |               
token   |string |       
name    |string |             
~~email~~|~~string~~|             

Response:

```
{
  "name" : "HERP DERP",
  "fbid" : "10156715917220243",
  "energy" : 5,
  "last_energy_update" : "1460588831566",
  "next_energy_update" : "2342342342342"
}
```


##Categories
`GET /categories`


Query parameters:

name    |type   |possible values
--------|-------|---------------
fbid    |string |               
token   |string |               

Response:

```
[{
  "id" : 1,
  "name" : "Politics"
},
{
  ...
}]
```


##Topics
`GET /topics/getForNewConversation`


Query parameters:

name    |type   |possible values
--------|-------|---------------
fbid    |string |               
token   |string |         
category|integer|

Response:

```
{
  "id" : 2,
  "side_a" : "Clinton",
  "side_b" : "Sanders",
  "side_a_url" : "http://image.png",
  "side_b_url" : "http://another_image.jpg",
  "category" : 1
}
```

`GET /topics/all`


Query parameters: None

Response:

```
[
{
  "id" : 2,
  "side_a" : "Clinton",
  "side_b" : "Sanders",
  "side_a_url" : "http://image.png",
  "side_b_url" : "http://another_image.jpg",
  "category" : 1
},
{
  "id" : 2,
  "side_a" : "Clinton",
  "side_b" : "Sanders",
  "side_a_url" : "http://image.png",
  "side_b_url" : "http://another_image.jpg",
  "category" : 1
},
{
  "id" : 2,
  "side_a" : "Clinton",
  "side_b" : "Sanders",
  "side_a_url" : "http://image.png",
  "side_b_url" : "http://another_image.jpg",
  "category" : 1
}
]
```


##Queuing
`POST /queue/addOrMatch`


Body fields:

name    |type   |possible values
--------|-------|------------------
fbid    |string |                  
token   |string |                  
topic_id|integer|                  
side    |string |`side_a`, `side_b`

Response:

```
{
    "room_name" : "2",
    "topic_id" : 1, 
    "user_a" : "12312312123123",
    "user_b" : "12312312312313",
    "end_time": 1460597361000
}
```


##Conversations
`GET /conversations/getForUser`


Querystring parameters:

name          |type  |possible values
--------------|------|--------------------------
fbid          |string|                          
token         |string|                          
states        |array |`active`, `review`, `done`
includePending|string|`true`,`false`

Response:

```
[{
  "room_name" : "room_name",
  "topic" : {
    "id" : 2,
    "side_a" : "Clinton",
    "side_b" : "Sanders"
  },
  "category" : {
    "id" : 2,
    "name" : "Politics"
  },
  "user_a":"10156715917220243",
  "user_b":"10156715917220244",
  "score_a" : 0,
  "score_b" : 0,
  "status" : "active|review|done",
  "start_time" : "1458174995",
  "end_time" : "1458174995" //epoch time
},
{
  ...
}]
```


##Arbitration
`GET /conversations/getForJudge`


Response:

```
{
  "room_name" : "room_name",
  "topic" : {
    "id" : 2,
    "side_a" : "Clinton",
    "side_b" : "Sanders"
  },
  "category" : {
    "id" : 2,
    "name" : "Politics"
  },
  "user_a":"10156715917220243",
  "user_b":"10156715917220244",
  "score_a" : 0,
  "score_b" : 0,
  "status" : "active|review|done",
  "start_time" : "1458174995",
  "end_time" : "1458174995" //epoch time
}
```

`POST /converstations/updateScore`

Querystring parameters:

name             |type  
-----------------|------
fbid             |string
token            |string
pubnub_room      |string
score_a          |int
score_b          |int

Response:
```
{}
```

