package org.onehippo.forge.konakart.replication.utils;

import org.hippoecm.repository.api.StringCodec;
import org.hippoecm.repository.api.StringCodecFactory;

public class Codecs {

    private static StringCodec display = new StringCodecFactory.IdentEncoding();

    private static StringCodec node = new StringCodecFactory.UriEncoding();

    public static String localizeName(String name) {
        return display.encode(name);
    }

    public static String encodeNode(String name) {
        return node.encode(name);
    }

}
