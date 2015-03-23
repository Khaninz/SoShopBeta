package soshop.social.soshop.Utils;

/**
 * Created by Ninniez on 12/13/2014.
 */
public final class ParseConstants {
    //Class SoShopPst

    public static final String CLASS_SOSHOPPOST = "SoShopPost";
    public static final String KEY_RELATION_POST_SENDER = "sender";
    public static final String KEY_RECIPIENT_IDS = "recipientIDs";
    public static final String KEY_POST_SENDER_ID = "senderID";
    public static final String KEY_SENDER_FIRST_NAME = "senderFirstName";
    public static final String KEY_SENDER_LAST_NAME = "senderLastName";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_ITEM_PRICE = "itemPrice";
    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_CAPTION = "senderCaption";
    public static final String KEY_TOTAL_SOSHOP = "soshopVote";
    public static final String KEY_TOTAL_NOSHOP = "noshopVote";
    public static final String KEY_IS_VOTE_SOSHOP_RELATION = "isVotedSoShop"; //user that voted with SoShop
    public static final String KEY_IS_VOTE_NOSHOP_RELATION = "isVotedNoShop"; //user that voted with NoShop
    public static final String KEY_IMAGE_I = "itemImage1";
    public static final String KEY_IMAGE_II = "itemImage2";
    public static final String TYPE_IMAGE = "image" ;
    public static final String KEY_LOCATION_DESCRIPTION = "itemLocationDescription";
    public static final String KEY_IS_PRIVATE = "isPrivate";
    public static final String KEY_RELATION_COMMENT = "comment";
    public static final String KEY_COMMENT_NUMBER = "commentNumber";

    //CLASS USER
    public static final String CLASS_USER = "User";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_RELATION_FRIENDS = "friendsRelation"; //Relation KEY
    public static final String KEY_RELATION_SOSHOP_VOTE = "voteSoShopByUser"; // post that user have vote Noshop
    public static final String KEY_RELATION_NOSHOP_VOTE = "voteNoShopByUser"; // post that user have vote Noshop
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";

    //CLASS COMMENT
    public static final String CLASS_COMMENT = "Comment";
    public static final String KEY_COMMENT_TEXT = "commentText";
    public static final String KEY_RELATION_COMMENT_SENDER = "sender";
    public static final String KEY_RELATION_TARGET_POST = "targetPost";
    public static final String KEY_COMMENT_SENDER_ID = "senderId";
    public static final String KEY_SENDER_OBJECT = "sender";


}
