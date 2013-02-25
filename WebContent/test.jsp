<%@page contentType="text/html;charset=UTF-8" %>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.example.logic.*" %>
<%

CmdX01 cmdX01 = new CmdX01();
cmdX01.execute();
// TODO: escape xml
out.println(cmdX01.getMax() );

out.print("<br/>");

CmdX02 cmdX02 = new CmdX02();
cmdX02.setCol1("Z");
cmdX02.setCol2(new BigDecimal(3) );
cmdX02.execute();
//TODO: escape xml
out.println(cmdX02.getHeader() );

%>