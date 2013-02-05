<?php

namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class PerformanceTestType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('test_name', 'text')
            ->add('test_description', 'textarea')
            ->add('test_developer_name', 'text')
            ->add('test_uri', 'text')
            ->add('performanceTestParameters', 'collection', array('type' => new
            		PerformanceTestParametersType(),
            		'allow_add' => true,
            		'allow_delete' => true,
            		'by_reference' => false))
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Societies\TestEngineBundle\Entity\PerformanceTest'
        ));
    }

    public function getName()
    {
        return 'societies_testenginebundle_performancetesttype';
    }
}
