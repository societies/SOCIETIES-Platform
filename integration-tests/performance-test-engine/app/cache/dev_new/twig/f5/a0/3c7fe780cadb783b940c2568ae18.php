<?php

/* AcmeDemoBundle:Demo:contact.html.twig */
class __TwigTemplate_f5a03c7fe780cadb783b940c2568ae18 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("AcmeDemoBundle::layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'content' => array($this, 'block_content'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "AcmeDemoBundle::layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        echo "Symfony - Contact form";
    }

    // line 5
    public function block_content($context, array $blocks = array())
    {
        // line 6
        echo "    <form action=\"";
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("_demo_contact"), "html", null, true);
        echo "\" method=\"POST\" id=\"contact_form\">
        ";
        // line 7
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'errors');
        echo "

        ";
        // line 9
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getAttribute($this->getContext($context, "form"), "email"), 'row');
        echo "
        ";
        // line 10
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getAttribute($this->getContext($context, "form"), "message"), 'row');
        echo "

        ";
        // line 12
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'rest');
        echo "
        <input type=\"submit\" value=\"Send\" class=\"symfony-button-grey\" />
    </form>
";
    }

    public function getTemplateName()
    {
        return "AcmeDemoBundle:Demo:contact.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  289 => 101,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  221 => 86,  193 => 66,  185 => 64,  177 => 62,  173 => 61,  166 => 59,  162 => 57,  154 => 53,  124 => 31,  95 => 24,  105 => 35,  54 => 14,  21 => 1,  38 => 6,  51 => 22,  47 => 11,  299 => 105,  293 => 103,  290 => 95,  287 => 100,  285 => 93,  280 => 90,  274 => 86,  271 => 85,  268 => 84,  266 => 83,  261 => 80,  247 => 79,  243 => 77,  228 => 75,  220 => 73,  218 => 72,  213 => 70,  209 => 69,  202 => 69,  196 => 63,  183 => 61,  181 => 63,  175 => 58,  158 => 55,  107 => 41,  101 => 34,  80 => 24,  63 => 16,  36 => 6,  156 => 58,  148 => 55,  142 => 50,  140 => 50,  127 => 45,  123 => 44,  115 => 42,  110 => 40,  85 => 28,  65 => 19,  59 => 24,  45 => 11,  103 => 28,  91 => 20,  74 => 22,  70 => 14,  66 => 18,  25 => 4,  89 => 20,  82 => 45,  92 => 32,  86 => 31,  77 => 23,  57 => 15,  19 => 2,  42 => 9,  29 => 5,  26 => 3,  223 => 87,  214 => 90,  210 => 88,  203 => 84,  199 => 83,  194 => 80,  192 => 62,  189 => 65,  187 => 77,  184 => 76,  178 => 72,  170 => 67,  157 => 61,  152 => 52,  145 => 50,  130 => 48,  125 => 49,  119 => 45,  116 => 44,  112 => 26,  102 => 36,  98 => 33,  76 => 24,  73 => 23,  69 => 20,  56 => 12,  32 => 7,  24 => 3,  22 => 3,  23 => 3,  17 => 1,  68 => 30,  61 => 18,  44 => 7,  20 => 2,  161 => 63,  153 => 50,  150 => 49,  147 => 51,  143 => 46,  137 => 45,  129 => 42,  121 => 47,  118 => 29,  113 => 41,  104 => 35,  99 => 33,  94 => 21,  81 => 20,  78 => 19,  72 => 16,  64 => 15,  53 => 10,  50 => 10,  48 => 13,  41 => 7,  39 => 7,  35 => 5,  33 => 5,  30 => 4,  27 => 3,  182 => 70,  176 => 71,  169 => 60,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 51,  141 => 54,  136 => 47,  134 => 50,  131 => 43,  128 => 47,  120 => 37,  117 => 36,  114 => 27,  109 => 25,  106 => 29,  100 => 30,  96 => 8,  93 => 7,  90 => 28,  87 => 22,  83 => 24,  79 => 29,  71 => 21,  62 => 17,  58 => 17,  55 => 12,  52 => 15,  49 => 14,  46 => 9,  43 => 9,  40 => 7,  37 => 5,  34 => 4,  31 => 4,  28 => 3,);
    }
}
