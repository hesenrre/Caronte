class TestController < Controller::Base
  
  def say
    render :text => "hola desde jruby"
  end
  
end
