<?php

namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class ManagementInfoType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('engine_host')
            ->add('test_mode')
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Societies\TestEngineBundle\Entity\ManagementInfo'
        ));
    }

    public function getName()
    {
        return 'societies_testenginebundle_managementinfotype';
    }
}
