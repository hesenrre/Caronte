
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/stylesheets/caronte.css">
  </head>
  
  <body>
    <h1>Caronte Search & Deletion Tool</h1>    
    <div id="content"></div>
    <div id="appname"><%= request.getContextPath()%></div>
    <script type="text/javascript" src="<%= request.getContextPath() %>/javascripts/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/javascripts/jquery.simplemodal-1.4.1.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/javascripts/caronte.js"></script>
  </body>
</html>
