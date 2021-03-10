<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2021/3/2
  Time: 15:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register Page</title>
</head>
<body>
    Hello MVC!
    <form action="<%=request.getContextPath()%>/register/doRegister" method="post">
        <label for="name">Username: </label><input id="name" value="" name="name" /><br>
        <label for="password">Password: </label><input id="password" value="" name="password" /><br>
        <label for="email">Email: </label><input id="email" value="" name="email" /><br>
        <label for="phoneNumber">PhoneNumber: </label><input id="phoneNumber" value="" name="phoneNumber" /><br>
        <input type="submit" value="Submit">
    </form>
</body>
</html>
