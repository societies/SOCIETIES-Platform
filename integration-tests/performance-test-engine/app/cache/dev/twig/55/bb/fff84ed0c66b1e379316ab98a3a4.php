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
        return array (  89 => 20,  82 => 19,  72 => 16,  64 => 15,  55 => 14,  53 => 13,  49 => 12,  42 => 13,  34 => 7,  23 => 4,  103 => 24,  96 => 22,  94 => 21,  91 => 20,  87 => 19,  81 => 18,  74 => 16,  70 => 14,  66 => 12,  48 => 8,  31 => 5,  25 => 4,  22 => 2,  20 => 2,  223 => 96,  214 => 90,  210 => 88,  203 => 84,  199 => 83,  194 => 80,  192 => 79,  189 => 78,  187 => 77,  184 => 76,  178 => 72,  176 => 71,  170 => 67,  161 => 63,  157 => 61,  155 => 60,  152 => 59,  145 => 55,  141 => 54,  136 => 51,  134 => 50,  130 => 48,  128 => 47,  125 => 46,  119 => 45,  116 => 44,  112 => 43,  102 => 36,  98 => 34,  93 => 31,  76 => 28,  73 => 27,  69 => 26,  61 => 24,  58 => 23,  56 => 9,  39 => 9,  32 => 11,  17 => 1,  92 => 39,  86 => 6,  79 => 40,  77 => 17,  57 => 22,  46 => 19,  37 => 6,  33 => 7,  29 => 6,  24 => 3,  19 => 2,  44 => 7,  41 => 7,  30 => 4,  27 => 3,);
    }
}
