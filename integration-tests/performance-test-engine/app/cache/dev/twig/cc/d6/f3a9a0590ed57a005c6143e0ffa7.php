<?php

/* WebProfilerBundle:Profiler:toolbar_js.html.twig */
class __TwigTemplate_ccd6f3a9a0590ed57a005c6143e0ffa7 extends Twig_Template
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
        echo "<div id=\"sfwdt";
        echo twig_escape_filter($this->env, $this->getContext($context, "token"), "html", null, true);
        echo "\" class=\"sf-toolbar\" style=\"display: none\"></div>
";
        // line 2
        $this->env->loadTemplate("WebProfilerBundle:Profiler:base_js.html.twig")->display($context);
        // line 3
        echo "<script type=\"text/javascript\">/*<![CDATA[*/
    (function () {
        Sfjs.load(
            'sfwdt";
        // line 6
        echo twig_escape_filter($this->env, $this->getContext($context, "token"), "html", null, true);
        echo "',
            '";
        // line 7
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("_wdt", array("token" => $this->getContext($context, "token"))), "html", null, true);
        echo "',
            function(xhr, el) {
                el.style.display = -1 !== xhr.responseText.indexOf('sf-toolbarreset') ? 'block' : 'none';
            },
            function(xhr) {
                if (xhr.status !== 0) {
                    confirm('An error occurred while loading the web debug toolbar (' + xhr.status + ': ' + xhr.statusText + ').\\n\\nDo you want to open the profiler?') && (window.location = '";
        // line 13
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("_profiler", array("token" => $this->getContext($context, "token"))), "html", null, true);
        echo "');
                }
            }
        );
    })();
/*]]>*/</script>
";
    }

    public function getTemplateName()
    {
        return "WebProfilerBundle:Profiler:toolbar_js.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  42 => 13,  29 => 6,  24 => 3,  22 => 2,  17 => 1,  115 => 42,  113 => 41,  110 => 40,  106 => 29,  103 => 28,  96 => 8,  93 => 7,  82 => 45,  80 => 40,  68 => 30,  66 => 28,  59 => 24,  55 => 23,  51 => 22,  34 => 10,  32 => 7,  21 => 1,  53 => 10,  50 => 9,  45 => 12,  43 => 9,  38 => 6,  35 => 5,  28 => 3,  299 => 105,  293 => 103,  289 => 101,  287 => 100,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  223 => 87,  221 => 86,  202 => 69,  193 => 66,  189 => 65,  185 => 64,  181 => 63,  177 => 62,  173 => 61,  169 => 60,  166 => 59,  162 => 57,  158 => 55,  154 => 53,  152 => 52,  149 => 51,  145 => 50,  124 => 31,  118 => 43,  114 => 27,  112 => 26,  109 => 25,  95 => 24,  87 => 5,  81 => 20,  78 => 19,  61 => 18,  58 => 17,  52 => 15,  48 => 13,  46 => 20,  36 => 6,  33 => 7,  27 => 5,);
    }
}
