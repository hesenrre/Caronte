<%@page
	import="com.fastsearch.esp.search.result.IQueryResult,com.fastsearch.esp.search.result.IDocumentSummary"%>
<%@page
	import="com.fastsearch.esp.search.result.IDocumentSummaryField,java.util.Iterator,org.apache.commons.lang.StringUtils"%>
<%
	IQueryResult result = (IQueryResult) request.getAttribute("result");
%>
<br>
<div id="resultheader">
	<%
		if (result.getDocCount() > 0) {
	%>
	<input type="button" id="deleteb" value="Delete now" />
	<input type="button" id="schdeletb" value="Scheduled Delete" />
	<%
		}
	%>
	<label id="total"><%=result.getDocCount()%>
		documents found
	</label>
</div>
<%
	if (result != null && result.getDocCount() > 0) {
%>
<%
	for (int i = 1; i <= result.getDocCount() && i <= 10; i++) {
%>
<%
	IDocumentSummary doc = result.getDocument(i);
%>
<h4 class="resulttitle"><%=doc.getDocNo()%>
	-
	<%=doc.getSummaryField("title").getStringValue()%>
	<span class="showmore" doc="doc<%=i%>">Show more</span>
</h4>
<p class="resultteaser"><%=doc.getSummaryField("body").getStringValue()%></p>
<div id="doc<%=i%>" class="resultbody">
	<ul>
		<%
			for (Iterator<IDocumentSummaryField> it = doc
							.summaryFields(); it.hasNext();) {
		%>
		<%
			IDocumentSummaryField field = it.next();
		%>
		<%
			if (!field.getName().equals("title")
								&& !field.getName().equals("body")) {
		%>
		<li>
			<span><%=field.getName()%></span>
			<p><%=StringUtils.defaultIfEmpty(
									field.getStringValue(),
									"&lt;empty field&gt;")%>
			<p>
		</li>
		<%
			}
		%>
		<%
			}
		%>
	</ul>
</div>
<%
	}
%>
<%
	}
%>
 <script type="text/javascript" src="<%= request.getContextPath() %>/javascripts/caronte_cron.js"></script>