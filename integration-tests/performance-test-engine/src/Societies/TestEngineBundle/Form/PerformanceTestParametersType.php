<?php

namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class PerformanceTestParametersType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('parameter_name', 'text')
            ->add('parameter_type', 'text')
            ->add('parameter_description', 'textarea')
            ->add('parameter_unit', 'text')
            ->add('parameter_value', 'textarea')
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Societies\TestEngineBundle\Entity\PerformanceTestParameters'
        ));
    }

    public function getName()
    {
        return 'societies_testenginebundle_performancetestparameterstype';
    }
}
