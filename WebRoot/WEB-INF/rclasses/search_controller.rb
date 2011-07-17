include_class "com.jwm.hermes.Search"
include_class "com.fastsearch.esp.search.query.Query"
include_class "com.fastsearch.esp.search.query.BaseParameter"
class SearchController < Controller::Base
  
  def index
    
    begin
      
      print "on begining\n";
      s = Search.new("192.168.10.13:45100","multimediossppublished")
#      s = Search.new("valhalla.lainsoft.org:15100","testsppublished")
      result = s.search(build_query(params["query"],params["navigation"]))
      request["query"] = params["query"]
      request["totalcount"] = result.documentCount
      request["hits"]= result.hits
      request["navs"] = result.navigators
      request["navigation"] = java.net.URLEncoder.encode(params["navigation"]) if params["navigation"]
    rescue java.lang.Exception
      render :text => "Search is not working #{$!}"
    end
    
  end	
  
  private
  def build_query(query="",navigation=nil)
    query = Query.new("contentcum:string(\"#{query}\", mode=\"SIMPLEALL\", annotation_class=\"user\")")
    query.setParameter(BaseParameter::LANGUAGE, "en")
    query.setParameter(BaseParameter::HITS, 10)
    query.setParameter(BaseParameter::OFFSET, 0)
    modif = []
    if navigation
       print "navigation '#{navigation}'\n"
       navigation.strip.split("PBNAVPB").each do |mod|
        modif << mod.split("PBMODPB")[0]
      end
      print "modifs '#{modif.join(" ")}'\n"
      query.setParameter("navigation", modif.join(" "))
    end
    return query
  end
  
end