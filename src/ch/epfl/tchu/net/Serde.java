/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        21 avr. 2021
 */

package ch.epfl.tchu.net;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;

/**
 * Interface représentant un objet capable de sérialiser et désérialiser des
 * valeurs d'un type E
 * 
 * @author ahmedkallala
 *
 * @param <E>
 *            le type des valeurs à sérialiser ou désérialiser
 */
public interface Serde<E> {
    /**
     * Méthode qui sérialise l'objet "toSerialize" donné en paramètre
     * 
     * @param toSerialize
     *            l'objet à sérialiser
     * @return la sérialisation de "toSerialize" sous forme d'une chaine de
     *         caractères
     */
    public abstract String serialize(E toSerialize);

    /**
     * Méthode qui désérialise la chaine de caractères "toDeserialize" donnée en
     * paramètre
     * 
     * @param toDeserialize
     *            la chaine de caractères à désérialiser
     * @return l'objet dont la sérialization correspond à la chaine de
     *         caractères "toDeserialize"
     */
    public abstract E deserialize(String toDeserialize);

    /**
     * Méthode qui retourne un serde permettant de (dé)sérialiser un objet au
     * moyen des fonctions de serialisation et déserialisation données en
     * paramètres
     * 
     * @param <E>
     *            le type de l'objet à (dé)sérialiser
     * @param(Function<E, String>) serializer la fonction de serialisation
     * @param(Function<E, String>) deserializer la fonction de déserialisation
     * @return le serde permettant de (dé)sérialiser un objet de type E
     */
    public static <E> Serde<E> of(Function<E, String> serializer,
            Function<String, E> deserializer) {
        return new Serde<E>() {
            public String serialize(E toSerialize) {
                return serializer.apply(toSerialize);
            }

            public E deserialize(String toDeserialize) {
                return deserializer.apply(toDeserialize);
            }
        };
    }

    /**
     * Méthode qui retourne un serde permettant de (dé)sérialiser un objet de la
     * liste donnée en paramètre
     * 
     * @param <E>
     *            le type des objets contenus dans la liste "values"
     * @param values(List)
     *            liste dont on souhaite (dé)sérialiser un des objets
     * @return un serde permettant de (dé)sérialiser un objet de "values"
     */
    public static <E> Serde<E> oneOf(List<E> values) {
        return new Serde<E>() {
            public String serialize(E toSerialize) {
                return Integer.toString(values.indexOf(toSerialize));
            }

            public E deserialize(String toDeserialize) {
                return toDeserialize == "" ? null
                        : values.get(Integer.parseInt(toDeserialize));
            }
        };
    }

    /**
     * Méthode qui retourne un serde permettant de (dé)sérialiser une liste en
     * utilisant le serde et le séparateur donnés en paramètre
     * 
     * @param <E>
     *            le type des objets de la liste à (dé)sérialiser
     * @param serde
     *            le serde correspondant au type des éléments de la liste
     * @param separator
     *            le séparateur se trouvant entre chaque élément sérialisé de la
     *            liste
     * @return un serde permettant de (dé)sérialiser une liste
     */
    public static <E> Serde<List<E>> listOf(Serde<E> serde, char separator) {
        return new Serde<List<E>>() {
            public String serialize(List<E> toSerialize) {
                StringJoiner serialized = new StringJoiner(
                        String.valueOf(separator));
                for (E s : toSerialize) {
                    serialized.add(serde.serialize(s));
                }
                return serialized.toString();
            }

            public List<E> deserialize(String toDeserialize) {
                List<E> deserialized = new ArrayList<>();
                String[] serialized = toDeserialize
                        .split(Pattern.quote(String.valueOf(separator)), -1);
                for (String s : serialized) {
                    if (!s.isEmpty()) {
                        deserialized.add(serde.deserialize(s));
                    }
                }
                return deserialized;
            }
        };

    }

    /**
     * Méthode qui retourne un serde permettant de (dé)sérialiser un objet de
     * type SortedBag en utilisant le serde et le séparateur donnés en paramètre
     * 
     * @param <E>
     *            le type des objets de la liste à (dé)sérialiser
     * @param serde
     *            le serde correspondant au type des éléments du SortedBag
     * @param separator
     *            le séparateur se trouvant entre chaque élément sérialisé du
     *            SortedBag
     * @return un serde permettant de (dé)sérialiser un SortedBag
     */
    public static <E extends Comparable<E>> Serde<SortedBag<E>> bagOf(
            Serde<E> serde, char separator) {
        return new Serde<SortedBag<E>>() {
            public String serialize(SortedBag<E> toSerialize) {
                StringJoiner serialized = new StringJoiner(
                        String.valueOf(separator));
                for (E s : toSerialize) {
                    serialized.add(serde.serialize(s));
                }
                return serialized.toString();
            }

            public SortedBag<E> deserialize(String toDeserialize) {
                SortedBag.Builder<E> deserialized = new SortedBag.Builder<E>();
                String[] serialized = toDeserialize
                        .split(Pattern.quote(String.valueOf(separator)), -1);
                for (String s : serialized) {
                    if (!s.isEmpty()) {
                        deserialized.add(serde.deserialize(s));
                    }
                }
                return deserialized.build();
            }
        };
    }
}
