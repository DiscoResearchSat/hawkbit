TARGET_BUILD_FOLDER="./hawkbit-runtime/hawkbit-update-server/target/"
java -jar $(find $TARGET_BUILD_FOLDER -name "hawkbit-update-server*-SNAPSHOT.jar") --spring.profiles.active=discosat,users
