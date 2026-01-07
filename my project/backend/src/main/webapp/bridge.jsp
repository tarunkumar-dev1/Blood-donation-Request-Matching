<%@ page contentType="application/javascript" pageEncoding="UTF-8" %>
(function(window){
  var base = '<%= request.getContextPath() %>/api';
  window.backendConfig = {
    baseUrl: base,
    auth: base + '/auth',
    donors: base + '/donors',
    matches: base + '/matches',
    contact: base + '/contact',
    health: base + '/health'
  };
})(window);
