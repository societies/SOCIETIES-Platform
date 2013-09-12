<?php
/**
 * 
 * @author     Fylhan
 * @copyright  Fylhan
 * @license    Fylhan
 */
require_once('DBAccessor.class.php');

class DAO {
	/**
	 * Accessor de BDD
	 * @var DBAccessor
	 * @access protected
	 */
	protected  $dbAccessor;


	/**
	 * @access public
	 * @param array $params 
	 */
	public  function __construct($params=array()) {
		$this->dbAccessor = DBAccessor::getInstance();
	}


	/**
	 * Exécute une requête et renvoie le résultat
	 * @access public
	 * @param string $qry requête à préparer
	 * @param array $params Paramètres de la requête
	 */
	public  function query($qry, $params) {
		try {
			$requete = $this->dbAccessor->getDb()->prepare($qry);
			if (!$requete) {
				throw new DAOException('Erreur lors de la préparation de la requête SQL : '.$qry, 2);
			}
			$res = $requete->execute($params);
			if (!$res) {
				throw new DAOException('Erreur lors de l\'exécution de la requête SQL : '.$qry, 3);
			}
			return $requete;
		}
		catch(PDOException $e) {
			throw new DAOException('Erreur lors de la prépration de la requête SQL : '.$qry, 4, $e);
		}
	}

	/**
	 * Exécute une requête et renvoie un tableau d'objet résultat
	 * @access public
	 * @param string $qry Reqête SELECT à exécuter
	 * @param array $params Paramètres à passer à la requête
	 * @param string $class Classe des éléments
	 */
	public  function select($qry, $params, $class) {
		$requete = $this->query($qry, $params);
		require_once(INCLUDE_PATH.'/data/'.$class.'.class.php');
		$data = $requete->fetchAll(PDO::FETCH_CLASS, $class);
		return $data;
	}


	// setters / getters
	/**
	 * 
	 * @return 
	 */
	public function getDbAccessor()
	{
	    return $this->dbAccessor;
	}
	/**
	 * 
	 * @param $dbAccessor
	 */
	public function setDbAccessor($dbAccessor)
	{
	    $this->dbAccessor = $dbAccessor;
	}
}
?>