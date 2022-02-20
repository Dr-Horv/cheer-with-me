# Backend for Cheer With Me

## Tech
REST backend Using Kotlin with Ktor framework (https://ktor.io/docs/welcome.html)

Overall the intention is to experiment with an event sourced model

https://martinfowler.com/eaaDev/EventSourcing.html

https://www.youtube.com/watch?v=8JKjvY4etTY

SQRS
https://martinfowler.com/bliki/CQRS.html


## Running
Right now the setup is in need of some love to run from command line. 
Recommended way is to open `build.gradle` from IntelliJ, the `main`-function 
is located in `Application.kt` create a run configuration from that and
add the following environment variables:
```
export GOOGLE_CLIENT_ID=100813085034-huu6nmbj7uicgik0r6ms9oe90j51drl0.apps.googleusercontent.com; \
export GOOGLE_CLIENT_SECRET=askHorvOrTejp \
export APPLE_CLIENT_ID=asd; \
export APPLE_CLIENT_SECRET=asd; \
export IOS_PUSH_ARN=asd; \
export ANDROID_PUSH_ARN=asd
```

`
docker build -t cheerwithme .
`

`docker run --env-file .env.example -p 8080:8080 cheerwithme`


