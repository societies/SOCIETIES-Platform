<?php

/* TwigBundle:Exception:error.json.twig */
class __TwigTemplate_5b6a691a74f2474d05e5c4b6575ac28f extends Twig_Template
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
        echo twig_jsonencode_filter(array("error" => array("code" => $this->getContext($context, "status_code"), "message" => $this->getContext($context, "status_text"))));
        echo "
";
    }

    public function getTemplateName()
    {
        return "TwigBundle:Exception:error.json.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  23 => 5,  17 => 1,  68 => 14,  61 => 9,  44 => 14,  20 => 2,  161 => 32,  153 => 50,  150 => 49,  147 => 48,  143 => 46,  137 => 45,  129 => 42,  121 => 41,  118 => 40,  113 => 39,  104 => 36,  99 => 33,  94 => 31,  81 => 25,  78 => 24,  72 => 22,  64 => 20,  53 => 16,  50 => 15,  48 => 14,  41 => 9,  39 => 8,  35 => 8,  33 => 5,  30 => 9,  27 => 6,  182 => 70,  176 => 66,  169 => 62,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 53,  141 => 48,  136 => 45,  134 => 44,  131 => 43,  128 => 42,  120 => 37,  117 => 36,  114 => 35,  109 => 38,  106 => 37,  100 => 30,  96 => 32,  93 => 28,  90 => 29,  87 => 26,  83 => 24,  79 => 22,  71 => 19,  62 => 19,  58 => 8,  55 => 12,  52 => 6,  49 => 10,  46 => 15,  43 => 8,  40 => 12,  37 => 11,  34 => 5,  31 => 7,  28 => 3,);
    }
}
