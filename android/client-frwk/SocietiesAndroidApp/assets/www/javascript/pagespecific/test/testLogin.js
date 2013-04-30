var assert = require('assert'),
  login = require('./../login.js');

suite('Login.js', function(){
  setup(function(){
    // ...
  });

  suite('validateLoginCredentials', function(){
	    test('should return false with no username', function(){
	        assert.equal(false, login.validateLoginCredentials('', 'password', 'xmppserver'));
	      });
	    test('should return false with no password', function(){
	        assert.equal(false, login.validateLoginCredentials('name', '', 'xmppserver'));
	      });
	    test('should return false with no server', function(){
	        assert.equal(false, login.validateLoginCredentials('name', 'password', ''));
	      });
	    test('should return false with no credentials', function(){
	        assert.equal(false, login.validateLoginCredentials('', '', 'server'));
	      });
	    test('should return false with no arguments', function(){
	        assert.equal(false, login.validateLoginCredentials('', '', ''));
	      });
  });
});
