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
        return array (  115 => 42,  113 => 41,  110 => 40,  106 => 29,  103 => 28,  96 => 8,  93 => 7,  82 => 45,  80 => 40,  68 => 30,  66 => 28,  59 => 24,  55 => 23,  51 => 22,  34 => 10,  32 => 7,  21 => 1,  53 => 10,  50 => 9,  45 => 12,  43 => 9,  38 => 6,  35 => 5,  28 => 3,  299 => 105,  293 => 103,  289 => 101,  287 => 100,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  223 => 87,  221 => 86,  202 => 69,  193 => 66,  189 => 65,  185 => 64,  181 => 63,  177 => 62,  173 => 61,  169 => 60,  166 => 59,  162 => 57,  158 => 55,  154 => 53,  152 => 52,  149 => 51,  145 => 50,  124 => 31,  118 => 43,  114 => 27,  112 => 26,  109 => 25,  95 => 24,  87 => 5,  81 => 20,  78 => 19,  61 => 18,  58 => 17,  52 => 15,  48 => 13,  46 => 20,  36 => 6,  33 => 5,  27 => 5,);
    }
}
