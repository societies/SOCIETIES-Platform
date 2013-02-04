<?php

/* SocietiesTestEngineBundle:Administration:test_node_form.html.twig */
class __TwigTemplate_4d8e2fec16125fb1ff9b527fbfc7234a extends Twig_Template
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
        echo "<div class=\"well\">
\t<form method=\"post\" ";
        // line 2
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'enctype');
        echo ">
        ";
        // line 3
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'widget');
        echo "
        <input value=\"save\" type=\"submit\" class=\"btn btn-primary\" />
    </form>
</div>";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:test_node_form.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  24 => 3,  20 => 2,  17 => 1,  45 => 10,  43 => 9,  36 => 6,  33 => 5,  27 => 3,);
    }
}
