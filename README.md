## Gitlab Webhooks Handler

- 在`application.yml`中配置 `shell.dir` 以及 `token`
- 将所有的 shell 脚本放到 `shell.dir` 中（记得 `chmod +x`），每个脚本对应一个项目，脚本名应与项目名一致
- 编译运行

```
mvn clean package -DskipTests
java -jar target/gitlabhook-0.0.1-SNAPSHOT.jar
```

→_→ 杀鸡要用牛刀
