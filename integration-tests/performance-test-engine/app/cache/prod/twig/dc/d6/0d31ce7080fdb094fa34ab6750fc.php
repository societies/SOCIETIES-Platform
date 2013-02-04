<?php

/* ::base.html.twig */
class __TwigTemplate_dcd60d31ce7080fdb094fa34ab6750fc extends Twig_Template
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
        echo "<!DOCTYPE html>
<html>
    <head>
        <meta charset=\"UTF-8\" />
        <title>";
        // line 5
        $this->displayBlock('title', $context, $blocks);
        echo "</title>
        ";
        // line 6
        $this->displayBlock('stylesheets', $context, $blocks);
        // line 7
        echo "        <link rel=\"icon\" type=\"image/x-icon\" href=\"";
        echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("favicon.ico"), "html", null, true);
        echo "\" />
    </head>
    <body>
        ";
        // line 10
        $this->displayBlock('body', $context, $blocks);
        // line 11
        echo "        ";
        $this->displayBlock('javascripts', $context, $blocks);
        // line 12
        echo "    </body>
</html>
";
    }

    // line 5
    public function block_title($context, array $blocks = array())
    {
        echo "Welcome!";
    }

    // line 6
    public function block_stylesheets($context, array $blocks = array())
    {
    }

    // line 10
    public function block_body($context, array $blocks = array())
    {
    }

    // line 11
    public function block_javascripts($context, array $blocks = array())
    {
    }

    public function getTemplateName()
    {
        return "::base.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  40 => 10,  332 => 105,  321 => 101,  317 => 100,  314 => 99,  300 => 98,  290 => 96,  283 => 94,  278 => 93,  260 => 92,  257 => 91,  250 => 89,  242 => 86,  208 => 65,  203 => 64,  193 => 62,  188 => 61,  183 => 60,  180 => 59,  176 => 57,  162 => 51,  157 => 50,  125 => 27,  104 => 24,  94 => 22,  42 => 11,  114 => 35,  98 => 32,  72 => 20,  115 => 42,  110 => 40,  96 => 8,  66 => 28,  21 => 1,  53 => 10,  51 => 5,  49 => 13,  331 => 100,  325 => 103,  322 => 95,  318 => 94,  315 => 93,  310 => 90,  304 => 86,  301 => 85,  297 => 84,  294 => 83,  289 => 80,  275 => 79,  271 => 77,  256 => 75,  246 => 87,  243 => 72,  237 => 70,  232 => 69,  224 => 66,  214 => 62,  200 => 61,  197 => 60,  190 => 58,  169 => 56,  163 => 52,  154 => 50,  136 => 31,  132 => 47,  129 => 29,  121 => 26,  113 => 41,  80 => 40,  74 => 21,  59 => 15,  52 => 14,  139 => 45,  124 => 42,  118 => 25,  109 => 34,  99 => 33,  84 => 24,  81 => 23,  73 => 20,  69 => 18,  62 => 10,  41 => 7,  123 => 24,  108 => 20,  95 => 18,  90 => 31,  87 => 20,  83 => 23,  26 => 4,  34 => 4,  102 => 34,  78 => 23,  61 => 17,  56 => 14,  38 => 6,  92 => 27,  86 => 24,  46 => 12,  37 => 5,  33 => 7,  29 => 7,  19 => 1,  44 => 12,  27 => 5,  55 => 23,  48 => 14,  45 => 12,  36 => 6,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 69,  218 => 63,  216 => 79,  213 => 66,  210 => 77,  207 => 76,  198 => 63,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 51,  155 => 51,  153 => 50,  149 => 47,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 22,  112 => 37,  107 => 31,  85 => 28,  82 => 19,  77 => 39,  67 => 11,  63 => 14,  32 => 7,  24 => 6,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 55,  168 => 53,  165 => 52,  156 => 51,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 44,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 21,  106 => 29,  103 => 28,  100 => 27,  97 => 28,  93 => 7,  89 => 16,  79 => 12,  68 => 30,  64 => 18,  60 => 22,  57 => 6,  54 => 15,  50 => 13,  47 => 11,  43 => 9,  39 => 9,  35 => 5,  31 => 6,  28 => 3,);
    }
}
