package net.doge.sdk.entity.music.info.lyric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MrcDecoder {
    private static final long DELTA = 2654435769L;
    private static final int MIN_LENGTH = 32;
    private static final char SPECIAL_CHAR = '0';
    private static final String KEY = "karakal@123Qcomyidongtiantianhaoting";

    public static String decode(String data) {
        return (data == null || data.length() < MIN_LENGTH) ? data : new String(toByteArray(TEADecrypt(toLongArray(data), toLongArray(padRight(KEY, MIN_LENGTH).getBytes(Charset.forName("UTF8"))))), Charset.forName("UTF-16LE"));
    }

    private static long[] TEADecrypt(long[] data, long[] key) {
        int len = data.length;
        if (len >= 1) {
            long j2 = data[0];
            for (long j3 = (6 + 52 / len) * DELTA; j3 != 0; j3 -= DELTA) {
                long j4 = 3 & (j3 >> 2);
                long j5 = len;
                while (--j5 > 0) {
                    long j6 = data[(int) (j5 - 1)];
                    int i = (int) j5;
                    j2 = data[i] - (((j2 ^ j3) + (j6 ^ key[(int) ((3 & j5) ^ j4)])) ^ (((j6 >> 5) ^ (j2 << 2)) + ((j2 >> 3) ^ (j6 << 4))));
                    data[i] = j2;
                }
                long j7 = data[len - 1];
                j2 = data[0] - (((key[(int) ((j5 & 3) ^ j4)] ^ j7) + (j2 ^ j3)) ^ (((j7 >> 5) ^ (j2 << 2)) + ((j2 >> 3) ^ (j7 << 4))));
                data[0] = j2;
            }
        }
        return data;
    }

    private static long[] toLongArray(byte[] data_) {
        int i;
        byte[] bArr = new byte[data_.length * 2];
        for (int i2 = 0; i2 < data_.length; i2++) {
            bArr[i2 * 2] = data_[i2];
            bArr[i2 * 2 + 1] = 0;
        }
        i = bArr.length % 8 == 0 ? 0 : 1;
        int len = i + bArr.length / 8;
        long[] jArr = new long[len];
        for (int i3 = 0; i3 < len - 1; i3++) jArr[i3] = bytesToLong(bArr, i3 * 8);
        byte[] bArr2 = new byte[8];
        int i4 = 0;
        for (int i5 = (len - 1) * 8; i5 < bArr.length; i4++, i5++) bArr2[i4] = bArr[i5];
        jArr[len - 1] = bytesToLong(bArr2, 0);
        return jArr;
    }

    private static byte[] toByteArray(long[] data) {
        List<Byte> arrayList = new ArrayList<>();
        for (long j : data) {
            byte[] long2bytes = longToBytes(j);
            for (int i = 0; i < 8; i++) arrayList.add(long2bytes[i]);
        }
        while (arrayList.get(arrayList.size() - 1) == SPECIAL_CHAR) arrayList.remove(arrayList.size() - 1);
        byte[] bArr = new byte[arrayList.size()];
        for (int i2 = 0; i2 < bArr.length; i2++) bArr[i2] = arrayList.get(i2);
        return bArr;
    }

    private static byte[] longToBytes(long num) {
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        order.putLong(num);
        return order.array();
    }

    private static long bytesToLong(byte[] b, int index) {
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        order.put(b, index, 8);
        return order.getLong(0);
    }

    private static long[] toLongArray(String data) {
        int length = data.length() / 16;
        long[] jArr = new long[length];
        for (int i = 0; i < length; i++)
            jArr[i] = new BigInteger(data.substring(i * 16, (i * 16) + 16), 16).longValue();
        return jArr;
    }

    private static String padRight(String source, int length) {
        StringBuilder sb = new StringBuilder(source);
        while (sb.length() < length) sb.append(SPECIAL_CHAR);
        String source2 = sb.toString();
        return source2;
    }

    public static void main(String args[]) {
        String data = "5c5e965153f142127cf75d713e54e7450baaeaba2ff72f64b41d6cbb1eadc34835b15d0d8973dee55adcd411348ca4ccc1416b3c95b625582b05ab06e7859ee8a2527f41e474c1decb67421591be396079db7709ca4980a25efed855c3b4d0f960251fbcc377f25d57cd4a0bb6090e90c2a541b7de0bf55379fbb77c7980bf371ac21b9d8ef7b25254af69e59c10180806ccc582fd83e4e7706cbca85cb34717672847fb2ef1f084a4d8ec71c420325a67a21d4890740e518e041df903ad43c926005d57affd7b05d86cf47612aacf9af36c7a026864b622b2f674431e061b65b9a25be94484cde4df04fbc8370e630b219e872128fde8d0ebec602c53e341c6b7c65b56c6f51186d0076f45169a700a7c379c49ee361a7ebf78f6959fb268922475e64c5c1ef6ffa6fad2c1cb3217c0e5d26094bd80e4c93c6b065971a8d7eb0c79d6092ad133752cddb5e09fa1f634a6271a259bb8a747feb6bb6032905404a239283f4954dc70815401512c6a9793de0bf744fea11577df893d0cdb4df3e2c40135110d24ba47c1f72d185cb3f5adb40867a2de3b2b7f212eedb884c0aa03d0dc8c749dd87486cdec79f4ad5072beae3b76f93c68817af8926825e3611eb9045effb8237299e7c5d4529249a43b4280a8efc1cc8e00a843c4730bfa1e9c21b4c0b9df6761fc388c07d180894e7a7bca43a0d663a3be1340ec8c9d7c5a0885c0004217738666bd967ee0feb4ae3f77a68e47b2d8800e629071fca751ee516a019c26561a80b08cfbfbb9da0d256d39bb2c3a66fbe343816a6f52be600e4d042217df3f3dde277b2f77409adecfab09963a3c292e5d8036a2494c37d676d62148cf3fdf8b01366e6b15f07b0c5821e9dda5ffd6ff2617c65dfd2fce58669dcb17d69b7c16175dbf757887a7f561f95468619081c1bd4446141de426a8372086f238570a64815c9ff83b2af1ecc1c20c5bc370c72038f1729bec294059566cfb87a011001a1e7c17ea8156be60fb55c8112b216894f4c8bae4ff30596ea51514460b3cd79a2bcab6ecc4f88cc95464ccd478586aee036fe8cae4648bd8a880b1199ac3e7f3f4d347b620b993d62868b4538b3588ce2666ff9a9106cb1828531384a0cb9fa273f2e220a4aa9b21bf54db6573bcf680a12fd7200fc28a50a40ebb1aa7c93f00c7777bb807230ef0c166f9834152f0c43aeb261505e422031e46cdd30fb263a7e1f29f59fbc3f013a76d88d9f5f2244e947435768ea6b24ded69593bf8fa9597198bb62c4ebca547028ea341146efe7f9ec4bff37135ecc794b1312a585ce239c42814d4a818694ec6d994c0a6347b6e44304715732d8c81f1fe8f72e19b59b5ec9f0c1e60266fa875f2c39d623cfadfc316463713b348cb75f239b0d1afbf6c977f9f000033ce4eac2c80ca825bc77b7c47366b337b47c0352fa7ac8e0b3aefb560aeef382c8e6e412d9fa1209c2f1a1fbbdc085d455ef6bc8f3eb4f0eff28a7c19036c2a620d97a77d5e573cc55e432004237294031f0e815cdbe613e07c3b6fa66ded589699db3e18117a34989e51418cca305a41e7da51312b745b7a20479aaed696d0ea54054c764b5db456a5113deebdf2ee9a2a633cb0b36e1b5bd7cf08668882f4595052f1842f885bebf9d6ad34e3c1dfd77f3c424fef4a96786d00c460d2892aa3088bd29d8374adeca126f952e250f2768300f6e82c4a97e94263f49f70767005f02e100e0da644283009fe101cfe8c1d438a70e7d1b5ac8f028152f8f8cd040d8611157093003772b26e5feeb9c42dbd2b9818f4ec960aa7ed66b416851ef1097db9cb1ac6c34dab4a1d91e1bae7a5e83895438000270abceea4d18ede833112a14b761af0fdc2e798308bb558bf67853e49d769e9437975f104a1ff243e7155de08bce09ec8d9d00c254021362fc9791570d3739f8cba869749892d2f64f79649cef3b368d25d6a4ab0d672cee78c9a7c03050a5f3275fd8bfab07d04993685c3245a11e1ba22f407bad1bed957567f7a2d6e0f5d1f86a8f228fce2f1da5ebe8c746ba6b8c2a7227b387121af90ca0eb72517181a8a3c3311afbb690f71c665933a488e5370e13a235b13db1e53820ec87f74216bdcab27120e29a411e5acd475d13877a78080121097f54a07f46e2b38607ac9341e7afe228e6cd0514cdf012f44917dd14c4d1b94b7be6c50ec7cc201a9cd9d9ca9326bd757a5f1687aa39b515576ce3fc7308a6a40cd858363dcc77c0470cffb5b340fff82f14986edeaeefb384d6b487f19c1dd66909b2c448af1f946de311cebc8ed4e1f016161915a0b297e901184c1a886ef93a8a68b9cf0d9ebe25f0f1a04f614a94cab22f3d15ba04ab22824a44e3ce8ced6c685911a47c9fedc046ff5ec94e1e2a5e5182bbd168f74aa119d41371e280009ad98cd2d084595fb69bd8a62067047dd5a0959ed3dd74a083f30ae926cea069a09e52f44942337fb33f0fbdaa14c37ad9712ce8078eb0208bb37dde84d0037b0c9afaff084869684c903133ba9c557728f7f272b9043610ded87d99798e19433633fe117eb954bf5892d77a81eb60a9ec9cfe96b6f4619b25a00aec0d95a00da02ec7a22a9b9d123863fa440e488cfe4bb8fc28771fe4b093c9391ca817454aa44bb97bbc33c70609bb7cae3eb85d314cead2530e6a73f1e0b27d4555d70693b6ed79da29cc4ad91bc844a32362a36e77f3cf2c17e39ce280fc841aa67845f15059bb4979de06a271562e4d6dc4da4eae638d327baa1883d0e0be42567586c033e8f89d98b56bfacfb072b6566ae13d99ee7bfdacab0e7f654f9c809c89817ebb2f5b27179ebcfba924e4151222fd83fa9a06a143f09ed28f421a13606e27004ac716ee8b198aa9aceaefaf8b8cda123037ea437d690d023309e8e37402d24389ad179e9b2cb27c364458a5ebe21f52692a8b6e35bcea35e4b97d9342341ba91f506b54ad8752bf10f44ac9dba5c2f9554e5b5ed9c8c2ba2151de614964d9217250760cb6951ceb9464a11a89076ed74f7d651db4f5c4d83354f9746f4c34e7da896f4ee2f817f80bebef7b0734c89de728c9780dc5493b86413a23366ba36ab95f0326662d46f766f36985f79ec38f6d6245eb15e0708f2b3b43947df30ae4d12b6cabce2d3b070fc82abf7471a9d40ee83d9281935e30bbb841cab6eaf05d0d7ddfe05126c5cfa555ec2143b4ced9ab7aa85ce68522cc33047e4f6d16132987758a9c135c5ae34153391b898498bb903d81e5d38c4c2af08b4b37ce7a8a235ee4bd605da0b091061aa6dc4e09437e432b8e0789d96b1a02425ace0ac196d7ce9aa162ca6d5659bb86a01a79609c733e6d5303a703fba8d06c99a16ce921a576560b81fcf60d15f67705cb1609aeb97868666fbb8e043347357c1992434f7563be04bfaad08b95c7a2a876c04bee51c29350b924cd38af5e3b8c2a22872b1707520a98d46e1c4ec1e6169d4d5b6e4d161240429b86afcf9da454d00f3be8b981d1cb391788de7f98257b29a1dcef5977e2dd4ec5ea767644c3296297935cf065412b4e8d7909a48f29312eee69e08adab845829dbd9fc0094de984287b6a03230d009ed65479125764409264ff7bbac2d7bb4620488223bfb34772b9a213a98acaed8c11d0cb229b8f73693d3ff965a836ef39583063cc29992b0e0d01b314fc2fe41c1453318a07116dff8fb822ba818b0bc919e662265e142e8116f148817cfd5583e89c648016d70324deedaf07f0d00a4e55b1bfcbd526d6e374575cfa7f4f291f1030c8af0cc684a1a01dbb72754d886b53e1271040c9aabf93d5ff1b4675ee86c278834a02f2b998ae041fca80bb91cc4b1c144f637ea69a3646ff21441526125edc281b521656cf14c1485b7d00c923d440721412abca25fda59398320e71f8d298968926a561b46dac8a094b10f5e17f2ace3d7711357a3725e0131e5fb405152398a3aa8d1d506f0da4c82d710a45687de11fe9f2ec79d0f2b016f08cb03e8742051aa013f165d1d0aa67c57ee4c9532ba3106cdd8693ad5370972299ff20cd59df957ad726578e89efc4a79b776b8d9f7e01444a705d24e3e11cb8f4726dc27efae9a019e6ff15a4cfe3aa1f639da59578a7afef5258bc34cada84d5fd8f2f392db8e14a196c34f092c6f40a5d3b347bb4064190d51df652fed47024e2cef6b34fbb1476890d87cc595679e3e5caf42919868f01022179bcb5b277abb47a283b8dbdfc73f24c57f6fe8f9d0b0534bf66de44f8691c786dd56dc8300f40851092defa858db7d1c033558f9f7751b638a4547b30ea8067554b012a3c14126b8f7fb5eb5d789742d55849e8f1c27e20884289497f5357d5a3eda69927306206781e38f6790c955e16c9ee2b27a3196ffde560da89b4426550c2d605be20863a2512bbf5a77de44d7248b722e2a9a828761f3873917595e6bd9d31a52afb50f240e1d3799484e113728c1d8ade6c2b322c18b9656537b9ece518cdb69c0046d8eac92e0512cb091b0026f5b8bd28b9bceed94e89a250edb11500350bfe2d53a895382a0b8240f1e313b2868cfa4db25bb1988335fe4a9d432874cb0ff66654910602837347ac60b96bb88ff7372690154c9732e2b361ed664045b9be7a179c707a3ac22e15aa990f5a653785c1cf60521448ed754c0f2c01dd0afdc83f28eaf05434ebf13213d9e66d7cdb7a045b7b1d39d9bd0ed4024b3e0314458bb1b36a54b397168cc359d091d5c2964a409b8846225b017f0a67f4d7066eef9ab23a04793ce1f534b24f9f05020df30583417e8ddb3b0b8a0603a037cd56e673b2a03680c5c2507e4ed1caa192eed10ba9ce823eafe6b308296a48f9c667412086b74991a843b1d31e3ef7f61d43343b1e284ae47959520b94d972bd248b099faa5f9c4983a6fb5ba041c9f0785e54088d9dc5a694aea11bfb2ad1562bb7ab5489d09a9a1539fe985cb47bb97f080218381e1c8e3daaac95a4042be79083acbebf133942799ad5d4eb772fa6bfde803a5640aa3c441cb5424b322e7f372d4a5312a5ffefb1749ea1aaf41f06059aa5a56924038fa5f68907c41fc551c884ff2c59d93e8292787e911bc26289f1800297359a34ea3512da30c7a1af1c22180abdb8075f3ddc60ba2dad1de768f92e42b4feb0c25ac2056adc3e411e846010c8c5d71dfed28ac22b799ea301e48dca91fcaaed91e01d535246b0b7695c49c10e29950536b9d934945b0cd13a159bb2c3e023ff5a6a92f2145e9b045fb41d66d0a8f01a94cf1b4783f733191781784a881c49b3cf37009d24893890a68f043b0ec076e8835fe852d930f5700eb080a9edc177063be7c671451eedc4fd31d62a521a37cbcf701531e3fa15226d7f9e9ab5316508c939665404e965242cbc7542c40a3dfef64cfb4e03f207ed25cf19080c7c112efbeea962f5f579cd6b6fc988cdc60e95a574b7463db280d96ad42c5322ae41d598d6402bd0e3dc8104f4d88646a9a54c2f3fabc7cea8ca4f5844f39ab96e6e05802d30212b21d9b5161303edf0cbffcb0fccbac26f71fdb1255e484dbe10fa09d97c644e4dd9323f874d2d8ac3bf642fad8b71f36b79e79d6d298efc924abb336bbadc3a27fb50c6451806d85c9253cf259687ad9240e83c81f40a864bed60a70a5dbcc51a91796aa7f909c211784c71ae65c09b587572f269d278b3922f5b35372cd6185e0d027ee6b09175610d0bdf969e18a7e5615ac745bc5bb6a18f7589fd7b9be40bff993333355f7605a67b74c09d1cb852addd24d4b8e049d9ee0fbd99f50989258b8c47902e451ca1def5e59adccfb966e5ae1b7f7f39ee809c0f8f9dfc1e6ab4edad1951551e0a82a274c4205cad5b1403f50f6a812f750a99e4e793f060e0920baf670bd9af93c5eecba2705a8961d237659444a97343b05d9dbca7635678f94779818c438d66437506b4d735fa36197a018b303c682b3f34d6212913b99e665e5a2ad904e910d91c7fd1f1b0a7edf19318a36096d91dbf0d0085407ce93ab333cb040c64c7989cb9c8b6d16d09b14b05bfb4ef5716a841bf4bca8868441bc3c4927a5d5579294cbaba0dc033df2891c6283a25bb34d36d1c9fb74cf754e1a241519f5e9a9f1912c981ffa02b2c7fe08f6296be1d9eb275e92a0a9b6351a197c30fe3b72a7d1383985512a5a01b0dbc2258e7ff415aa1584666315ed5cbf73674bae661de783b8c818dc8a6663c412f99e623a31dc935f4b1ea245c4e134d9ce52b41bf307c7cde48da82a55b8606fa44535c5b502af8ab7c077b63bf77d26ff466b36989cb8cdbec1e16c6e60b27fa138b8300f055cd85df2c17ca2cad78eab896e2423ffd7db6b9ebc0627931f94cad88294729395c9c8d9b6750bf74db0ad25fa3b709c8672b7ce7202563a850822a218fa5aac10a38bf79d7ed0ed8c842e5630886181505200c520480ec3e80636c867edfac3c9e39c98c8334fdfc040182d7f95ba66488e4329091ba884cd202942cf1506e2b258b4d656500fb3387dffc0fc566278b78e1ec54e3b72e196b4796bd18d02febcc178c8b65b55e0a5d7bfb682c527f0cb66b8c8e1cfb117c5f0afdcb3b7fb2eadebe8dcebae48d40f52ffc7a7ab0b4bfe3c247a9c2cbe45c56988ad1a10bd55d7914885c8782e17b0f4d0346d16619b0f2661238796a3fbfef79849e628d42119c1a8af35fbcdf220075ec5827991ca9440f04e79dd484fbc9fb51385308d3dd339a4ae4f6af75d7d9b9fe0fb5d580b23db1788f744c7fb043c4b5020fea616499658e5da009da4598a2d57c951d5436a079154398405a433b435c1e1b5927ccaf759b5fd29147c48cce090d24d1c331e7a79c03f8015898e9cc3c1af6ba54d6761a39e524d2431ffe8d8a76a36bac62cb49f28dccf89357941284c8f0a64b1233b9ecc1d50a09da01672f7bd7196355849842e7688f48dbb653d28a3146c1daae39973a8e128335e8be7374e18e87647f5db0ef3ad8c1921a910beb3190c36d361afe05fe6efb2814adaefb60bf92d2a1e5162c8e3fed0951d7646301a9fa62309832fea045f9e26387d3572f361459c2db8d317cc37b962d42b82076950a38a089854c7c2b652605020ccd698b50dc8e0d03f7e83c94fb93f74897dbd9ef7fb778406155fc54efb01f49eb27411eb42bd6fae1b3cf5b470290da3ac2cc6564a3f85a0aaa90cfd098fd4b5bdac658c62222aeeefa9a219e7b52b314ec30ab9b0d8a0ffee50f6a55ac22e973479f8d00eba0608db793bf018efcb7904061820478e267073dc9ac6c7facc71c1e3e9dacc6053c6edbadf058bdc06f2b7fc7555d03e4ddd341d1da3066725605805c794eb6cfeb36776566bc6c169ffda8e406f690acc55057a8f664c056def43e5063b4b23cd807d00f3297195d274e7b68c8264765cae9b45ed56d8f6180cd4278d0cf9258901f203ee9478f544e8d55d6e845ba998a8c05ead1535735d28963611c75c08ea21a311a2f1443229e98cf78f47d1550151ef97ccc01acf5bea24cb5b38154483ea96350b1453091380722c539cbf0346cdb4369c98d8bd43a242f8f56ec3a7131a3123fae6546fe14b27832c8835c3d86be9ed9f7fb69cdef1ab66fc845cecc6a170ea55c6c77c8faa6e1994ea07b25311608e74961f055b525a3e8fdd102faf231a335de7c40484913c576904d748cc446dbf446d6ccc12dee3a300e8d846bd5cac4676739a73b3a980ca4ab6eacaffde7e6b03d8be78644cb1df88bb016d722d88d6514edb642ea71f6e1245055da918a61f1e1d10cf16fde0e980b9c2dc51627f1eb2bdbc5961f43eb64113a345ca5770b49a0ff10dc956e29d8c61e9536731bfec05795cbfcd24e4eafaebf9544de244f75de4e637b3984b2398ff3c9969637c5156e3455e8d587f0733a664a8266dee52c0b8b2c945cbeafe06d1bb1f1a1273dad343d32b4751e110af06de0de648f15bbc6babf0dcb66d37628deb7aeecfd0836a513fe27fdd297bcbf524881a545dcf4888cb2441edcc8035e8656767b75b4ad209e02399475edb9a48ab571f74a266b7fb6dcd393f62f36254a40c83c8b32181ef4985dd5a2d869569791a96d3a69a2ca23c921af13ea25e60e634dbc250dee0e91d50ac5a5dfac38ed6b302219f6516e1de671a25d02bfdb3b252fa57676aff4795e08f51dcf6ad35880d64e0d419cd3c97f08fd2172896adff180e0d70696403b3acfd7a582078fd8af8589a4a5950140d4cb20eaee61991450aff22e2846db49942eb79bdb1e1d6ff9eac27fc0a4debcde7365d7f69b3e9497b42011f14f9a74ddf8dea902afb4d1dd9de733f1d73439d42ea76ff39dae1f4cd930ed094722f537ce33d564c1774232afcdef2a35058627394ae14bc9064c54c6c3594e9421b8e5943f9f3476cdfc9e52ab37228cac30cfacc80069bdf21820cb063cc344d5b45bba0f38734e6359ccece28c5eec9f613332ca28e6003b1c68091aa12a30918dc1c24403cf0789502bf0f027688c28fe3d8cc4a147030836a9dd5a42a90cc82903aecaddd03e676230f000767434ef2c613fcb70440f49e5eb5fff7945d735596af54784df17042421ac840f43d6053016c3f8e5cbabd8f1dd2fabd485225778419716299094a7a56990bd5c3a5d885d4741b6688b9b407b230e5d5138eb23a9088a0e25a8296c3ec8aaf5b459f312abca421f943f0a9ae5f6d4989bc61747058acc051d8fbece718aa868bcf427ee36d81e6dd4c33fec93c4850df15ad22a5a271f4f6def747084cef9bf2aefc5bf79a95e5a16849489bae9153b022b18b4b0e601d88db5d8d3bd802cd1e345e965f23156ab34d483fdd778df0e376cea8dbae4023bb4044a17ec16c757462cc9319b63996b62621e4c0243ab34efbc3a7db2a74319b6945734da9fc2ea77432d7600e061f570e464197a6feb878ccdf0453c01de3ca2e0bdcc94eb0e13635adb5618e134352df3498a92e945b0333eb97647d9c4aa5924dacd7fd55687286b73e035f9f9d3e5fdb11ca38ec9eebebc67e9c397f693c4029d44427eed79e2ea0bbac59b4df5723f6b5795dd349ef1b5025d29008a19fad22107ff7aac5975f74fac03b5effafd5c11f2132d35fb867aec8e64126a8bf088e31128aba4aeacaa1b4f1d9adcacc6f7969601d960f4a7f545f85430b25321c90a03e852b0ffde8d9c018d723958f6dba44b997e449bbe91e80c2d74bea85790861be27462a7e783603d7ab10da08424e123086e4fd4a7f50c9fdaab3ac977fa98bc1569ecdf4b05a2db7a22ef276a7f65f0eb6a9d6291be1a1e7b8858fad071ad3f558e5450bb889a1fa9956c19ccbcab9d5c43823fde6094890bebbb26bfb9fa9b16041e6ca090234af14abfe742259c2dad7e7a71f907b2bfdbe70975121702a62b2d78a4208860b4031aaab0228f80dc4807872e08fabb7c98d2d925431915008e786edd326ab1771632debe05d1bbec61b873083834ec140ebb38ce7a8c52d961ae94de86e1f39028159b0a892e8e716ddef2eefd5314f9d153ba9ca1c5136a7eb710b4897a549620aadc50e4cf128cbafefd8b8d704647c261dd3c0d738c8840659a6222403da84bd38832d08ac2553555b15565b4568a06d23831ec1cec048fa7f1c4d15c5480c6f9ca17e34a2191eab404ad8d10f9d17f512bc9955ead14d1c610952302ffffffdb76b7c8cab78e78c5623b72df003eb3990b4afed050621f778680bfc1b88a5bdcf6aa1adef2a4c28af85a2ae90b15b3f6a6707883b4b59fbcde4352d6b3bda0080030b95f9a49eaed56ef76de1ff2b896a8111ea4764323b3d065eb9af4fcb2b923f6b6adf4f1443912f953ad79ebd9ec7e648e6bc6f53486c48d42fa5ce5f8cd01192cd58c62a6b9c4b39f00efef4037c7993cc31b43ccd694c2732e58a63a8fc7a4e4f8b560d9b846e236def237219a22686a31c98f3c8190a05867e12ea883cdc474032fd09a0268f72c662cd16b6679880cd1ffdb4aba14bee9b5439d7c090746a4d82c6a2b40a8f776bb40b9eaacb5f1475a0e299fe53979795e95a2864f7253816cbf487208ba988abfebd8fd918d7ff31a7dbcf1f3088d644ed18dfe6c2e7b2911d6a87147f772f38582cccee40e09655a0dc8938f6d15bba636091b6346f4a1674328e370e672dda61633fc9a4d330b19d3f812d5c170e0c0acfedd6c6117deeed40d6f8ccc081163d8aaef687f6fa0aed21710774e716704c58b77f5b9a70668fc32a50d364fa2f8236d28305e8e1880e946809b1a7b012279f713367a565aa8198b6a3f8afb3a82e967b6d82760d29060dc0db225b8c7baf2b7e3f9d03d362a362d7f561c1369d9f255f1cd129a48f5d4de66f294a85da0e6d26d33b27151a2a0b18fb7ec9a6c6421f64af04c913b7a31cd6f78a7019286695a371786573524bec01ecb5feacd1c743adae068043e761984d081bd793743313c5b441f075ff865cc17fd6b00e472cff9bd8726024cada7edb17419d768e1e06f3a84149c4c096c81450954a51c0b7dde5170d79efbcc67267365afd85180ace4343b3f8c9f51024fe89e20570319134305bfe28e2468ccb9930666db45096b8054820419b85ada22c49dfd31575b824ff3ae750d913fb0a28e32204c1ffd661d3672183dc874c2c3f47bb2832bd584c5f08707fc2d1fac37701072d554c9ec710c3422043724f6838cfb284ca7f88d302662bc4147be323ee502d87c51acf4e0982e9b66b4fcd31a2d7bf4ea81fcbd2d8e596e496f38bf40f8687c0477291e271af76fe4e128ce7482bc680662e4131ddf1131a26c8d4b3f5cd7aef3c39990f76bc56c2664ff5bd36183637f441a3644b89060c2cd5c97991a117e130368ef87180d7fe75eb29f9149662113a16ed2d0f0363ad178ec34465bd2d81583d600f49f02c824a522d91bd868359fa780797e0ff0cf42c13b9a61954fa06fc70cc1527d6d3f953bcbe7612b8165326c0a4d18c40c3ce3174edef5998f9c81e70bb81dc99d588640126355326300a2a4c2c27dffdcc628600a165dec49c289df7599db04925834b237856c95a9673cdca58f935e3e19e07a9604d3d87df0e73407f4284f07ec31e04f14aa41954054f6eb299ad3020179610b6ec15d5e7e3acad8124ac52f44b8da22cccabc1dac8a7ae2c1dd5072743ee7611039d9be8bd8f029b466c2e6fc095e05947165e249786f02e1c2107789b19a07f3a28662962c0a687d14ef152298fc83900979266d655b564849df0f2f51ec46d8c6acd04c80d009257fde73203d3660f195f33311dde89e3a0753213daedf5a3f62b9ad566fd2d1b8dde947f344d84d3c55987c5ebadcca25ca7f76c067a119b4d6c23177039a5afcf8af075062fc220f6cca98bd301d7d23e0460dd9a62aa0946c22ce621ac8f073b06bcd6c41d1f6f5c6857c128b70d6d9713de5293d11de9f0f285f49165328467c5499abfca1b8d3ec5e58a08a147a9fb2b850b34df11c8d001e78a050f4132fb088792ce2d6c37314e4342f18ec11a7995e39c54c202390735f6bb0d9b3ff1469e505456090f02e9cc46e8656d52e03bfc50bc8b66c92749d8eabb881ddffcb17df2d76b7780bad9b210ed5915f9accb6baa439401eb0c800c5aeb46b1db756de1f9f99b6404692c8059d7306c3fed2fb249954dad1329bcbaf1be289e5cb03356721a1ff30c3787e711846347b73554fea4f852170371a7cd8688c441dd6c44d76c4c59db3569298e5c8bbfb7a6c922486a9112c888c2120230f8185d84f403d38bf0d4d18d2e3f87d8ef554097f5e5de27a1ce96f6f6a2571a628147128582ae3b3b2bbcd0aa3d3d9657729e41fcccbffcf9ad868917a9a462b46831c3c7cb4ed4f21d69d2cc0c245c9c03fa08e845167f13c41cdf97556b4ade3f12bbe0c73e0a311f196033dbfbcbfbd4a7449a0b36d92c7e4e3f8085ce75fd51e15acbd649522d154a63e1fcd40dd4b8a368266ac98082d859dafea10c4d9d9b46cdb21ba29ec13b1c70ca388baa0cf50cb4ffa17e8bac19dca9afabffacaf79a8aeb5b12e862b4aef3be019523bb37c6a3ee532f38636aa7b19f9b5286071c22183bb4c59339c6c6d9c533cd09ee6280b3341459767d01c1bf8e90b76e5f5ab91b29a220f4f0f6ad0082ed3d976a7e6931fe7eda555aa961c057e76312d0c8edba0a213a00e4524bea228670056795ffca9801713a14d0b7f1dd8018adbdf1e1ee236ea6bbdd1db90bdb5fc3420827fdfc446ce380f983dde39ed1052c69dd16b7b5239896f66d05199630b738c66a73ddbb8b7bc8ccb3375471610348f382d154deac1f4f5d4c711a3f64cc0d1ac26a8d9a23b50c39e8620fa5a0027d2340e68b288e2d20fac9b34cffb8c43b398bbf71c6cf3951aa4404815544ce8ba8d8404164569a9a9707a58410f6299525eab13b414abe7ff9a006a4b1fb845eb09eb6dc5fad6d9507b2b22b428f9b4eca7e046b15b01a35fea1d0521a1d7cfad590c9f88afe46fd45b2b341376ea2bd7d069c7a2ec734b4a523252b1332e822e6046128bc7cd5c0e04b0bccd7d8321eff71d529a492804365cb2c7914baee853a356452eed3ec6dc417e4dbd97b558fa9909a227bbcee0231399aaba66ab2b0f7d756fa3962e3fea9fbdafda2edbd89bd5600a1a27b40fd04665a4ade3ac124aec9313f56d0bc9b0c3a00710f0e03e06a3f08546da18c2f3a917c6a7defcedd377dac07d80044b1da2cb1cfd6cd6186147d359ae2a027083d5d2a221b009327c66d940c70473fd4c186d207f80877f4a86bca36f4ea79e0fbfbcf96194ba5b69f4b06f95484702dfd566e9e29d3a6ea4af4e560b5e07364ed87e87f550a44ba0e9cff6e35ab6754fad696236ede8275e271a80327d6df85966b510567ae851b34b28e1baf589050b2fe14ef1d594dedde493ecb01b7db5e95786e3ec95da6b9a9ddd5e4d5a478859430397a4c54b4d9f2d69dc0bef31ac37b8ae961cb639b756f235224a677a24f1946a562a24339712a358a8acfc22141fc1481ef0a6ba846702652bcb0874b949cb04f024ac1cf8c31de779c20275ed605ca9c781166303ca2cc4c13be7d1d035ba493bf5ef21760418b3576603dfd69bc5d11383bf3a67526621238b287880863bc7c538969dfa1e15e3334645f5724308cd0a59548f04fc759949ca557fd4d278279519839bd15a3c80b92e4053a89dbb42751dce61b9bde092a4c33a5d9f0aa81f35069b2cd71a92cb7f20902f3c38dc86b4f5d4f10b5908848d94c6a2062deb71305d41fd067aab6264ace707463eab528a3873471932e5de490094b1487a2aa8917f49cb2f2ab6c26c67fcb572641acc3230cef0ec60d133e724b7f57779bf07e387352e8fdbe5d1d6867dd32b24f112acce790aa1d8ba3fb04687093191f4222548eb6b85319ee6a4643bdb7ed9972571abe3a6bec8be89c2bd782271f152ad94750f2d827b60ca9151d0e89b4289f1dec68fe953d6018fce7dad71794282c02636d735e679fd90523b2ead1b395b8dd4dc5edc76c18e65f1a072302cc28f5ac2d0ff5e1a23af27fcb4e10eeddbbb6bc5eb0fdb70e609afdd12ecee31386fb9e5be531d587c4a31b4f64bdbf148671d4c2b2f427c378e9da08c445d370b556972733238e7c3a777625cc5eba051f563c7b71eeb82112e413c3d8ba289a19003d6fadfe0db576d33b7cb20ba01d674cdbabf0e00906904c9896e6b7406699c6bf853be40d23211133d8db33da1214cd3e94f40cf0100363fff6398a4af55c13fd08b823a016db738b6a26d3ee4372c60942dcbc4f57b0758615846e430a469c3d120b5b649293779cbb17996b65ecda662528c424d36c7c0d891e8eed3504eead0e3b738fe1c154b9977a3a734a0f9202410d37b6c1670f5d89e63cda7760e3e6af42968395da47964d707b95353682e75da772c36f4b24c2569cd6fa1ceb10e8619f8131278d564b58ca677c355583985a3af504468a157f893c1c20c99e2a31d983a01e154411e0f78b0d2674b5f622076f4202c94fb608f71ae77c25ce5a36d77ced87a5fdf1e9996e7359660c7205c5e70d150119a0a4457126ec2b498f22a8e2994e435f7a939161fc197053d09a0b4c8cc8f6567fc6db4e4c6da4f789eae33cddb2d48ea4652f0862dde0eb84042487553c31a37483c56888339cb2c3725c4bb033a13e6d166c2595bf050b0897f292dd4f0dda1d94f6a4507f4a17ae69525aa9ebb58375e54e9dbe9d8c8e16b406beaeba57703a7479468c6ea796a3c4b260debe7b756231a21ed719deec6e67d8d5094677de16997d5a02dbd8f2e42fee8b96cd47e364456355c4f01d020a2991c0400378fccc946c4dd6cce163c57062ad77581ba89ec772812fbb5015b9f9b20ec963e63b4e730de699af85dd7eaf659c73870bd3ef7ce8d92f43cf7f858cf6336c155dce91a2bf671f66d5917cf92e65f4b0ed5645f58e9b95ce73b49581f31f41a448f642e942ed550b87431b842b1f46571af6a6d73a8ad49770468612b4e6a2b36af82e86d9583b1d673c4d84b718b2e2dd27264365471ad391ec4a0823c6a67e73375a36521e2ef612640e22834ac1336fb21ac96fccc7b3d9d718fa11c0a4a7d513424d4892001cdb30ac1dc31a178812736a8157621a5e5984685e09cf6b31da484053a96725200ba3b65215c0693eeccd4b21411871526aa03e1018edfbbfc9c42aae5db89e36661d5b548faf09a291bf2c8d94e97c5f46581e81c1a787b75ada929eaec6c2a0b3c70322e5a11f11b75e48176270e8bc7c71fbf77c0b62e329c9bea9b4cd5637c1b2f1f9c47d70d229e318bea0186b05ad7c300b9d60ee8ae67d36bb3a1b622a0a2aff68f994f3f86c822ac66022ba1dc81d653e077f864672eed88134d7a0c915c17e58942adbd15cb1d4d2ec168f81063162c46357b7dc321381729756eb691457ab067a7d3587846215267c74d133cc1d54d2061890ae50fa8591cdbb8cfb3c3068667854b3fc647905be9481d1c06c0dcaa7194d18965a7194a639f4a8348cfe43f23ad380bd6571328c750c25bbc36b824f3c05836a86bcc7fd4189f2407d6b67a8f70c7d4648840101ddb84f55b7d9bbe78a895a6356e8552909a4cec15254119afec645b3c75f75c02c90b6b3e926dc647f807e46664f281f2b098c4d34cfb6f12c7d937ec0ddc1b9116dced7a0d369f644904cb3fc307b185659a8c80fa75f47d608c711fe886f8f2248b73f13a880ca06f2efd2d74fa565e62dec590fb12770953a28424cb4f4c85580ebd949a7d160552acf5fdc8a0a2106d5f96f93f28db0a313c7ca78875d983472ee324d9dfd7a224b95002cdc37a609b5d208cc2d8d8fd660c0c83c63b9d49f4cc7b0351a35b456707731c58812c90a20d8ee9c8fad9d79b8bee073457b4d8b7a6a6347012f1979efe42adb4130d12078c39fb4a4430d9f4464982dfd94f1458f9fff22da40a9b6adedde63dda43565419da3494ccf68c741f00c45f9ff5b4ad0701fdd08c4d3a488bc5adee9980b685ebcae52e939308326b3834a2f1213cdf6b6c855418deeffaab021176c965ae026b8d20b3636f383a86d55adcb9b52d1858fdb6a250f59259882fcc5af04c892a76c0ac9f9d5e5b47fa173fbdde368a38715b15e140ee5914cb15a751d26ca451d2d974de9b0abfeada81dfba602b73cd74d5ef11bf664a3cbdea9a5cb803697499854d26b4b63078f42ff46a6839b28ec8f672499ff6e96a30d0fabedab3b797a65d1e0142e75d90ddfe806da95ad2be879e856d1b78e71f994460a7c98b493398a99e53adced86e3b35035703a859fccc8a7525844b43253d67e1810577d5b93e37739c0849a7d5b2b764422d0c5b04c9f842b0fa0650e6c558b0f7cb00c8aa265507212f6156e6ef29046cfc32d6d891c15cdea5298a8a6ccfb465eaa26cb391dd975fd168a81fe77e3132f640d6297e807e2471192863618859ebc63f7805af4d9e08a09c6dde6aaad4f748f0d1b478f7a534ad7e6fd2d87ee32051dfeb43ad1e26feb3e5b0a79100f4c9cf8ce0486f58e5a282e0f37a29582b146fc25e4dd21bb4cff8c5defb0a49e54d8f1883a6f10b0bb42fa3e99c2eb746451122e49ad52fc1b5c33a57815ff5591a3d6bb966f74eab38a4742a77b0a2840b648c370ac5e2c4723f716c1e60936fe33f03ab510fd1ce97c7e23811a67d39cd9f6a78008857f2959e0cb0869d1e2f259bead01d71ed8889c0dbfe896cbaa9c6143944b79ec44b2c232ccf0aecbed4d412d62e33028d7477552ce7cc54439dcca0139594798a60e353194d9f7c758d116d98035a897fdfa374c8974691545463bd9ada28fe8a87805e3abc98589b03b01a30ad27a10270c403ccd78dd478a08fd493fd60cab320ab14273217de319f44708d56f59e8ef394ab5db412429e856e65960ee8811a8aa63bea4ffeb463817bc25bb48f56ccac1301b0e88f1d8ca99f5ba18368b2f97b9288acffc2d149c11eec5bb7704a520dca5091a1f7fa90d8e3df4250c2cda728a0a20cc7a6ef4c309e5aee046e76b42932a5edc8a76fd882961a44a85aa22533a6162f25475a288620ddd8a7a507a77521f32c42aff3ce8edf8f8349fa44f1ef4e7dc10a6d821737bc4441cb477250d6046222eeaa358d1563cdb706cdd19144ffb090ff82b00cd171c630fd9a499d4b9c33dc66a9d570af8289024fff6b77f2144486c509dc0088227e12f46c25e8788dac29c7f829c4bb183c83bf1fc9b89e3872237112964c85c03eb5d4edfdd3039a30a7d1467deb38ac66aa0733de25299ba68565897b2ec2514ef5df1a52b058bbf06610afb317cb2f6701d6bf0321bc5c24117e67f3d2c8fd4d76a1f1df076d1ebb7f4c761b070f94dbcf456f007a1e23e27d98aee6773db77d8f9635e584c592d01b61a23e3a297da06000620fc82dcdf3a49505cbdd6156ac9618833012c01ef98cbe61d64996ddeb68a68f5d5cf2f841eb0e8eabf16bcd771f7f7fe55d257b867dd16022790271c6e8eda57cc1e283d8c4528f3a0a5be524600707caa0b6e7abea3188df92b506701f53d8c6a48bf19f924d1a26f306d162baa9cedd299802fed62a411e33a990ffef1494095c1fb0391bcb3d391607fe2dcab9bf52d924b3d89c24be8e0094c5749c4381df60fdd495c66854dcd7a4369da434e87608910f3c2984242ceed2aef93db130ddd6bb7481ca9cb951ef2f9675314e54211ed9ebec6f81119e734fc42556dbd09ad854f9d78fbb2b3ad787ebd41f82d2683a50240f80e779105e94fdd052754370b863d205e8c8fb6a0e12705678b284d139bdc2c58fec5d0f4fbba726a2be51ee77271d8c8be225cc92398f6e597f3ec97a0be6fb5a4334d14a4ae68ed055e5499268818b43e2ca8f6f99bfd49211bb8c0c79a451481388d64c6af9b7a8a92e8aaa0c771d04df026c09d0438f97cdbc0f611d283e471f9631664e8f81c40cd359b5947632dcb9821b68d76e885c96bf52618ef860327f1cee10b9e6cf78b67bd22122a193cfe9727db5c56ea88c8488f95b7211f934725ad39e8f0a9e7d50dc17b6c939629f41c9827e0b06bbd547c125a4fba112d6bce6ef65596fc365bef10ad4d8d855711cbba35111d34dc2e41b65d93fb2cb60bdc3ef9b551252dc6e82a8a049d9f83657e300065ebf17e963923a86c67fc14b9159446e2fa3d37b783a043da8dd6309e6c2e3697557afefd547e50e2c68fb05a217b268e6383f27a94154d0a6ee2ca651f30ee1eb887caa656be8ca1428f8448b29bdbc664bc097cd61bf609b696d7cbb7d3961f95b4600c567786b843ae31df1ba8af17c07710e35e95bf218bac30d8362ce0ceb8f876cd0529e08fc1ca5ec34762579500433ad481d9b7cf03d01355bb70a144a447840efdc10d228a8434ddbff9565bc80b1951792a7ec4e62814b43a3015212f85e6a31c11183908cd637987533478aec89cf198692aaa9362c38f09e325efe41b0b57894bfbed80dbc6072b8bf94ab5f112314619de0ab1e6c126ae6cdf3109f6ffac1bbed2c400766168ae4808fa41502aadabab49be741c4d682d523708a1154797b345fe96e1303d3967e7b454fab569070da26ba0a770f3ab051118ff9484d7965d45eb4bea49dc5c80014cad8f8127b63130fd44d02e878e304ff5a8c709f1ae24d93ae6568c4d87fefba0dfaf97be2ee86677dbbd78425d14072adf36f9ebd182e516495f3082cdecd9e499a72b3730e11fccba367bea60472cbcb6b85e6ecd511b3e1edcbdc1b61d1eb6c2cef1f5390d8e1854c672720fc8cea217b841f46d66d95826b821de4f8ffb8ae7271d21d65bb95b1544071f4e56150410de8f87d1b95b5721c72bee7d4f924f5d8928a01a19636b4638daefa845c76c28537e6700783ffdcb3d82df0fb55c99d8d47153fc299e6d6aa8fda2a23b6b44142d32a54beccd30de21cdc7c61544e022d45ca44ef621a17e3b68453dec15accfca5fe2b6fd0e2f218e225ed5838c65c7355ae7f2cc05aa45192e49cb3742666f1c07cd2fbb290ecb64b6104d3d971bd8e44a54df54cd579226189e7265a78a8478a7af4c26dbeb6fa20e3ed4702454aaf38b78336fb95168adb316d9df7d0aafd45126904d87ee298c2abe70d9aa7b635c697feb08768925e8013e891b4f108570aaa8a919a53f4b2b06f2a7b3995f0806595675f3c1407399eefedcb01fcb50122602c9bf4417070ff9c14365964ceb1e99b498d485ae3ecb05bacedade2fdd48b08da98be2e04582fd24a6abdb98e8b791b25f7e3baf966fbd5977230666aa1eb7095e3469e3505a951e540fe58e301b840bade5879de8de65edc724e65f6b5af1bbe23791d16017fbeb6a3de8fd9cfb318bab34c8d405babb981365b45a68f8180facfcbc26fa1d840e5a708317192959f5c39202fa3e884583e05c1c8c6720cfd196848798f8fdc40a30b86a8510e9beb9f31ed50785d3cba5e4a0a961190aa957b3a2e34c293fa3f491632d829acf779d680395194e83e1aae22b484cfbdfa131e7bf64065e64cb78b29267831f34295ca378392c25ec3ae1a7a59fcc26679b34cab2a380fc716c5f21bca6a114770f8bcfbf265993212594955d643cb7bf34c044da8600a0cf5097465f55f48f67a2df7e128a1e0c6cf0160d9804b918b3a53abceb7a97c0a7b3912390bd7985c1d43a36939a5b3a9614eae5c2f58d04cb6d33640d77997eba82c1cf30c84d1bd72b4a435c8a96e5a76a3f87dfa02b01256747988fafe591c705c83c4578db89b3b1db1f9cf2c7a56be9ef5599fea98baea86362c66afc45fd0781e0a3791320ae3e8ec3c7771fa6e7d4c107a32908a6b44cad34538bac1d8ce4eb6df525105309d54a67bbba5fb306a09c1850d584cd56cb5c7b3c72383ce1aff28f7bd88adf7cfca41c1bcfda80186c07ba90d75b73dd948b062e87278af332062755d5f71e55fa7763c27ad7ecafd307a4650add7cc8dd7fbe615b05870b3551c6268dfc2e88dfdc78619b850785311938fd045c7564afb8a17ae97c4cd127f13827e6baa46166573b42851bd27d84c1a8ce154d2b27f58e74e571eb9ef4926f9f8d268624a4a7c9c422a60df45782d343152168ae95130af70fc7ea1d1b8d7c184a6984f69e729edd99bcc61f330ff89f3f4c83c8edb569dc45e5340da6a9bf740733fcce17842dadadb705e50d51c7c049df552a3b6812322cffeff35e18d71c75e2117e46be2f3dee656ca7357c2070593c086bb5de82a2ffb86a62d023a64e2703a5b984f65abff665b44ccf532157528900efdcc8427dd7c0d02216b493a62dd01c6302db04dee0baf7514f2e7cb37cb12d7d55553a7a8883b814dfef1c44ef2bdf29e20f9414dacf26b94a19d8369a7603407aa69b9c7aa96445468009bc909af93a73b87ed4fe6f35b843c0f5f8ffabbf43b37477c5518885189181496b24dab578106894701b6ebd5e4d8a5d04ee693d0f9efc1ce182943acb5dcf3f05d37287f5b93a60c3df11f80b617baac297ed19f4070776d9265cbf7be489f5435aa1826756c9de4a999da001d864a5b4f2b744577477556487b200fe52d0ff1729e230225292c7d6f0471eff86a4c5f21a052486b97a1ab8c7db9c3823b4724701034dad0fc0c841df9eeb6432327d0182e6e1296e90caebb764464f06b7964901875a479e3b91cdec76777d25d33b69ed0dde73f5292d658f2bc697ba7a04fe6093d9cc4067b40340c2b855e31d949db10c7d4975322882150882af919db4a9d583ce200d57de207eae1482dc079c26c11bfb9ebb45e86897602f82ee628f6248fcf918bc092913e47f4e2b742c2d4eb0fce3dad2c569685819284c53242420baf4b73d06317d98218681a4a8b4a41d488555fd2b8bf79eadc87f47e815bdc8f0f0007fa6c35166365574a4db0992db5cdee150b6213e89a1c3a6277f601e3466721f391432bcf07fc3db2c8f29b217d2fcc17dbc76e39e446669b1520ce6e746b8421b3969ae604e64b6d4a1cd7c581e2c47ee404cd427f6ae63059b3dd981996e7bf4e32a9ae1339b3263bad82b946441e9cadd388119b9e70b69b2f49aa758ec0198cd863f53c305576a456621d7b7a18b856ee902d1519faf21859b728ab60e2aa1283e0b63f9c529e04803abd4de1c7f5965329af49ed5397873ffb1a6543358f96f882b561c6bcdd2e3319c066545740b6d648d8789638a184631de40ad7cefc88633837706336515ed20f74dc55e7b99be078f72428509fb7cd922e26a559f9d16401ff3fd680f3c86ceb3f042d7617291837a6d1c23fa5e2ca34fac4240bb88d0ee4d4008f53be4f69fa2ba794f2dd9583b50933669a3058155a517ab47a66e4c46bc925a55f73421d7b41c02828fa4d9c5407b09aa837bc88c2c7a78bfa733b0d85240e319b78ec5e543f48573d910f0937a3d17d848d5c8a25578e29706a1db5f771bcb4221dfee1df8312a0feb64d00151a065f902a140af5764db22d93b5ad7ad1c9f77da6935df26e24eb9cd02b6a3a1345b442caa2f1fee735ce04eadd25d9a33fef8547517557e9c31df44b14d581274cc4c57cca3fab88c0bf6bb1dddc80adc4bd33910c7c94e0440fc19dd6ccba599406d34a7d66b289ee33141e78360bdc7b43ecde2f4ffdb21db4be24590f5da3a8e62e4157480c1b8435b0e974f853d108b11bc3bfa8184956b6b86d34cfdfbf240dac8c59ab1847197e697f9f51d6e3ccb01f5300ada5566f9ef30de8a2e398c2e8ee2c6028b7d1a924105bb22772a6cefa7a509282e5bc717aafd781fe1a7cb54df95d3b9ab08ebc33b5be224d7c7780a530bfec406cedfe25a0f77314195c6395d3c90401ff7560aa4e29f45a34c25a9a6a9a00f712589774c6ad629b3376ca7ba07da310f6e9d923046e5d68b93e8746b2ed01c54c187d48d8e5898d1e37ea8e72572f767de2039fff417e39a71959fa12080793ffeac5567edd04db207dfc5605a7c9a239ea145600e9f4b3e6ee4bb90a2ea777ae4f7aac3b1ef538b9d601c3ea3a201572c52241df912b729a9829c43e623b921bfeb3e56dc0e5f08e2cb41569b4f588d660689032e51367974f26c3aed6d083ba474554031cfb8821cb325c1fa3d8b5db890b5938c6b871211e30b335c5e2fecd434defb9bdbe20058091c1cc1b0e5c1f1ec9c7e907ad22bc77ae032745e57b4b61e2c561af90800617870d4797bbde5e967ca22358bc143171f06d96e1e2bdd1509c4b5e2a3ca2dc42e003b5ef1c824c01434bb412854856b48446498638fc478aaad4055f2d4a2d4936a2eb2bdc5ebd9a452c619de2d529281e60d7e3563f3488c25c51f5742626fc824332da0c498a17993bd4de69c4def5b9c37bcdcbe8fe6a9bd0a72d89bbfb85900aeb9681a1bd8d61928573f50ee7e983fc7be149591d69fb8a3f000a04c29e3c941f1840b78c5ad0d9c54ec261a0e473ef286f40b11ea361f7fb6ffdc67e365b1a7a6d736339aeb7d98ac2e67f51aebb74dfb4792478396d13da96889d51a03448275b95a430d1de67e434b0a58d5208841f567a17483501c3d1d8274ae55240a9ff5206ca1467cea10db52ac08c1a1f155905f59c92c1509d82c42a10aec162c6e0707a27cf6d58c5d55a1abf2e0d3b3c50f7cd2754147d3e556c33619d3ac7c1d475a9d45510e5388acfca7f44205c8621755422fe34e25d9d7fa45dfa5b9856f2a0c80236e6918a848e60cacffea099d4ee8173ea3afd485d91713623be2f347eadf68d761675b3e620c0ed1c4a70f6262908f219a55c368f90e5805fb4ffd6962ef1e4b9d9ccb87b6e477daf32e9cdeb7ff17ed860f7df9a796c2dd5aa99e4580eb75cb7bb432a9754a6c0ed0aec4f070b18f7abfed227afea680545ac98eba57526f150f12b6a101c87aa1a6fa4ac30595f9ad0e3bd1f8cede5b9dd8b0034b79ca2a39b34c67e4498057233168429a492e80233ad34ad0348a63ac12b0ab4e8b41827c2e451137caacde8a16e65b3f86f05363c5fdea17bf421a3919d2ad8e25763d48959b6b3cec4b9271b7902580b5d70a6f6aa9e2b6be4d0529be809cd46d92b0859e86d28e822358a2e45d54eed7db79c7b513ff1016b46c2434162f3703a4e720dada7b7fdff49d6dd37dd47817e8ec63c4f76c2cd9a95c8180dbabf66f97b127879ac6f3a0a6410750c3487d4b3222912ed844b2f62c458b500a6dd772fdafccecedcd79520a077407b4307e3f08ccad4ccb379ad669bb43433dd8098e50ada19fe17008d09bd30d22ba8fdcf11d323458a88ac42f43efbcd5bda62b533b923137529a14e01a71d0a77ecf25819903424daca73bf7637614352be09a41cf1c84931009983732711a74009f87296";
        System.out.println(decode(data));
    }
}
