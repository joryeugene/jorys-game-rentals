/* jshint ignore:start */

$(function () {

  // <!------------------------- Database Key ------------------------->
  var FINGERPRINT_KEY = "jorysGameflyUserFingerprint";

  function getUserFingerPrint() {
    var fingerprint = localStorage.getItem(FINGERPRINT_KEY);

    if (fingerprint === null) {
      new Fingerprint2().get(function (result) {
        localStorage.setItem(FINGERPRINT_KEY, result);
        return result;
      });
    } else {
      return fingerprint;
    }
  }

  // <!------------------------- Shopping Cart Modal ------------------------->
  var $cartModal = $('#cart-modal');
  var $cartModalBtn = $('#cart-modal-btn');
  var $cartModalBottomBtn = $('.modal-card-foot .button');
  var $cartModalClose = $('#cart-modal-close, .modal-background');
  var htmlTag = $('html');
  var isActiveClass = 'is-active';
  var isClippedClass = 'is-clipped';
  var keyUp = 'keyup';

  $cartModalBtn.click(function () {
    $cartModal.addClass(isActiveClass);
    htmlTag.addClass(isClippedClass);
  });

  $cartModalClose.click(function () {
    htmlTag.removeClass(isClippedClass);
    $cartModal.removeClass(isActiveClass);
  });

  $cartModalBottomBtn.click(function () {
    htmlTag.removeClass(isClippedClass);
    $cartModal.removeClass(isActiveClass);
  });

  $(document).on(keyUp, function (e) {
    if (e.keyCode == 27) {
      htmlTag.removeClass(isClippedClass);
      $cartModal.removeClass(isActiveClass);
    }
  });

  // <!------------------------- Main Game Search View ------------------------->
  var $searchBtn = $('#search-btn');
  var isLoading = 'is-loading';

  $searchBtn.click(function () {
    $searchBtn.addClass(isLoading);
  });

  // <!------------------------- Save Games To "Cart" ------------------------->

  var addToCartBtns = document.querySelectorAll('.add-to-cart-btn');

  for (var i = 0; i < addToCartBtns.length; i++) {

    addToCartBtns[i].addEventListener("click", function (e) {

      var identifierClass = e.srcElement.id.toString();
      var $gameName = $("strong#game-name." + identifierClass).get(0).innerText;
      var $gameDeck = $("p#game-deck." + identifierClass).get(0).innerText;
      var $gamePlatforms = $("small#game-platforms." + identifierClass).get(0).innerText;
      var $gameThumbUrl = $("img." + identifierClass).get(0).src;

      $.ajax({
        url: "/game/add",
        type: "get",
        data: {
          fingerprint: getUserFingerPrint(),
          name: $gameName,
          deck: $gameDeck,
          platforms: $gamePlatforms,
          thumbUrl: $gameThumbUrl
        },
        success: function (response) {
        },
        error: function (xhr) {
          console.log("Failed to add to cart");
        }
      });

    });

  }

});

/* jshint ignore:end */