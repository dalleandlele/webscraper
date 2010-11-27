<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.util.List"%>
<%@page import="it.data.db.Access"%>
<%@page import="it.data.db.ManagerDB"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Administration's Page</title>
</head>
<% List<Access> list = ManagerDB.getAccesses(); %>
<body>
    <table>
        <tr>
            <td>Date</td>
            <td>IP</td>
            <td>URL</td>
        </tr>
        <% for(Access a : list) { %>
        <tr>
            <td><%=a.toStringDate() %></td>   
            <td><%=a.getIpAddress() %></td>
            <td><%=a.getUrlRequest() %></td>     
        </tr>
        <%} %>
    </table>
</body>
</html>