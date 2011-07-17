class RubyTestController < Controller::Base
  def index
    render :text => "in ruby test"
  end
  
  def say
    render :text => "this is from ruby test"
  end 
  
end
