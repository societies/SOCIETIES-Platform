<?php

/* WebProfilerBundle:Profiler:base_js.html.twig */
class __TwigTemplate_55bbfff84ed0c66b1e379316ab98a3a4 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = array(
        );
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        // line 1
        echo "<script type=\"text/javascript\">/*<![CDATA[*/
    Sfjs = (function() {
        \"use strict\";

        var noop = function() {},
            request = function(url, onSuccess, onError, payload, options) {
                var xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP');
                options = options || {};
                xhr.open(options.method || 'GET', url, true);
                xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
                xhr.onreadystatechange = function(state) {
                    if (4 === xhr.readyState && 200 === xhr.status) {
                        (onSuccess || noop)(xhr);
                    } else if (4 === xhr.readyState && xhr.status != 200) {
                        (onError || noop)(xhr);
                    }
                };
                xhr.send(payload || '');
            },
            hasClass = function(el, klass) {
                return el.className.match(new RegExp('\\\\b' + klass + '\\\\b'));
            },
            removeClass = function(el, klass) {
                el.className = el.className.replace(new RegExp('\\\\b' + klass + '\\\\b'), ' ');
            },
            addClass = function(el, klass) {
                if (!hasClass(el, klass)) { el.className += \" \" + klass; }
            };

        return {
            hasClass: hasClass,
            removeClass: removeClass,
            addClass: addClass,
            request: request,
            load: function(selector, url, onSuccess, onError, options) {
                var el = document.getElementById(selector);

                if (el && el.getAttribute('data-sfurl') !== url) {
                    request(
                        url,
                        function(xhr) {
                            el.innerHTML = xhr.responseText;
                            el.setAttribute('data-sfurl', url);
                            removeClass(el, 'loading');
                            (onSuccess || noop)(xhr, el);
                        },
                        function(xhr) { (onError || noop)(xhr, el); },
                        options
                    );
                }

                return this;
            },
            toggle: function(selector, elOn, elOff) {
                var i,
                    style,
                    tmp = elOn.style.display,
                    el = document.getElementById(selector);

                elOn.style.display = elOff.style.display;
                elOff.style.display = tmp;

                if (el) {
                    el.style.display = 'none' === tmp ? 'none' : 'block';
                }

                return this;
            }

        }
    })();
/*]]>*/</script>
";
    }

    public function getTemplateName()
    {
        return "WebProfilerBundle:Profiler:base_js.html.twig";
    }

    public function getDebugInfo()
    {
        return array (  42 => 13,  29 => 6,  24 => 3,  22 => 2,  17 => 1,  115 => 42,  113 => 41,  110 => 40,  106 => 29,  103 => 28,  96 => 8,  93 => 7,  82 => 45,  80 => 40,  68 => 30,  66 => 28,  59 => 24,  55 => 23,  51 => 22,  34 => 10,  32 => 7,  21 => 1,  53 => 10,  50 => 9,  45 => 12,  43 => 9,  38 => 6,  35 => 5,  28 => 3,  299 => 105,  293 => 103,  289 => 101,  287 => 100,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  223 => 87,  221 => 86,  202 => 69,  193 => 66,  189 => 65,  185 => 64,  181 => 63,  177 => 62,  173 => 61,  169 => 60,  166 => 59,  162 => 57,  158 => 55,  154 => 53,  152 => 52,  149 => 51,  145 => 50,  124 => 31,  118 => 43,  114 => 27,  112 => 26,  109 => 25,  95 => 24,  87 => 5,  81 => 20,  78 => 19,  61 => 18,  58 => 17,  52 => 15,  48 => 13,  46 => 20,  36 => 6,  33 => 7,  27 => 5,);
    }
}
