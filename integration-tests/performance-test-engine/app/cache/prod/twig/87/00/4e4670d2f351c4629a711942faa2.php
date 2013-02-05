<?php

/* ::layout.html.twig */
class __TwigTemplate_87004e4670d2f351c4629a711942faa2 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'stylesheets' => array($this, 'block_stylesheets'),
            'body' => array($this, 'block_body'),
            'javascripts' => array($this, 'block_javascripts'),
        );
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        // line 1
        echo "<!DOCTYPE HTML>
<html>
  <head>
    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />
    <title>";
        // line 5
        $this->displayBlock('title', $context, $blocks);
        echo "</title>
    
    ";
        // line 7
        $this->displayBlock('stylesheets', $context, $blocks);
        // line 10
        echo "  </head>
  <body>
    <div class=\"container\">
      <div id=\"header\" class=\"hero-unit\">
        <h2>Societies Project </h2>
        <p></p>
      </div>
 
      <div class=\"row\">
        <div id=\"menu\" class=\"span3\">
          <h3><a href=\"";
        // line 20
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("home"), "html", null, true);
        echo "\">Home</a></h3>
          <ul class=\"nav nav-pills nav-stacked\">
            <li><a href=\"";
        // line 22
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin"), "html", null, true);
        echo "\">Management</a></li>
            <li><a href=\"";
        // line 23
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("test_engine"), "html", null, true);
        echo "\">Test Engine</a></li>
            <li><a href=\"";
        // line 24
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("test_results", array("page" => 1)), "html", null, true);
        echo "\">Test Results</a></li>
          </ul>
        </div>
        <div id=\"content\" class=\"span9\">
          ";
        // line 28
        $this->displayBlock('body', $context, $blocks);
        // line 30
        echo "        </div>
      </div>
 
      <hr>
 
      <footer>
        <p></p>
      </footer>
    </div>
 
  ";
        // line 40
        $this->displayBlock('javascripts', $context, $blocks);
        // line 45
        echo "  </body>
</html>";
    }

    // line 5
    public function block_title($context, array $blocks = array())
    {
        echo "Societies Project ";
    }

    // line 7
    public function block_stylesheets($context, array $blocks = array())
    {
        // line 8
        echo "      <link rel=\"stylesheet\" href=\"";
        echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("css/bootstrap.css"), "html", null, true);
        echo "\" type=\"text/css\" />
    ";
    }

    // line 28
    public function block_body($context, array $blocks = array())
    {
        // line 29
        echo "          ";
    }

    // line 40
    public function block_javascripts($context, array $blocks = array())
    {
        // line 41
        echo "    ";
        // line 42
        echo "    <script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js\"></script>
    <script type=\"text/javascript\" src=\"";
        // line 43
        echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("js/bootstrap.js"), "html", null, true);
        echo "\"></script>
  ";
    }

    public function getTemplateName()
    {
        return "::layout.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  115 => 42,  110 => 40,  96 => 8,  66 => 28,  21 => 1,  53 => 10,  51 => 22,  49 => 13,  331 => 100,  325 => 96,  322 => 95,  318 => 94,  315 => 93,  310 => 90,  304 => 86,  301 => 85,  297 => 84,  294 => 83,  289 => 80,  275 => 79,  271 => 77,  256 => 75,  246 => 73,  243 => 72,  237 => 70,  232 => 69,  224 => 66,  214 => 62,  200 => 61,  197 => 60,  190 => 58,  169 => 56,  163 => 52,  154 => 50,  136 => 49,  132 => 47,  129 => 46,  121 => 42,  113 => 41,  80 => 40,  74 => 19,  59 => 24,  52 => 14,  139 => 45,  124 => 42,  118 => 43,  109 => 34,  99 => 33,  84 => 25,  81 => 24,  73 => 20,  69 => 19,  62 => 16,  41 => 7,  123 => 24,  108 => 20,  95 => 18,  90 => 28,  87 => 5,  83 => 23,  26 => 4,  34 => 10,  102 => 34,  78 => 23,  61 => 13,  56 => 12,  38 => 6,  92 => 27,  86 => 24,  46 => 20,  37 => 6,  33 => 5,  29 => 7,  19 => 1,  44 => 12,  27 => 5,  55 => 23,  48 => 7,  45 => 12,  36 => 6,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 83,  218 => 63,  216 => 79,  213 => 78,  210 => 77,  207 => 76,  198 => 71,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 51,  155 => 51,  153 => 50,  149 => 47,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 22,  112 => 37,  107 => 31,  85 => 28,  82 => 45,  77 => 39,  67 => 17,  63 => 14,  32 => 7,  24 => 6,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 57,  168 => 54,  165 => 53,  156 => 51,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 44,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 21,  106 => 29,  103 => 28,  100 => 27,  97 => 28,  93 => 7,  89 => 16,  79 => 12,  68 => 30,  64 => 13,  60 => 22,  57 => 15,  54 => 12,  50 => 9,  47 => 10,  43 => 9,  39 => 9,  35 => 5,  31 => 4,  28 => 3,);
    }
}
