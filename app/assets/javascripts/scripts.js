/* jshint ignore:start */

// <!------------------------- Database Key ------------------------->
var FINGERPRINT_KEY = 'jorysGameflyUserFingerprint';

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

// <!----------------- Load Shopping Cart Data On Page Load ----------------->
$.ajax({
  url: '/cart/count',
  type: 'get',
  data: {fingerprint: getUserFingerPrint()},
  success: function (response) {
    changeCounter(response);
    populateCartItems();
  },
  error: function (xhr) {
    console.log('Failed to add to cart');
  }
});

// <!--------------------- Shopping Cart Icon Counter ---------------------->
var $counterNum = $('span.icon.is-large.fa-stack.has-badge').get(0);

function changeCounter(num) {
  $($counterNum).attr('data-count', num);
}

// <!------------------------- Shopping Cart Modal ------------------------->
var $cartModal = $('#cart-modal');
var $cartModalBtn = $('#cart-modal-btn');
var $cartModalBottomBtn = $('.modal-card-foot .button');
var $cartModalClose = $('#cart-modal-close, .modal-background');
var $shoppingCartContents = $("#shopping-cart-contents").get(0);
var htmlTag = $('html');
var isActiveClass = 'is-active';
var isClippedClass = 'is-clipped';
var keyUp = 'keyup';

function populateCartItems() {
  $.ajax({
    url: '/cart',
    type: 'get',
    data: {fingerprint: getUserFingerPrint()},
    success: function (response) {
      $shoppingCartContents.innerHTML = response;
    },
    error: function (xhr) {
      console.log('Failed to load cart');
    }
  });
}

function emptyCart() {
  $.ajax({
    url: '/cart/empty',
    type: 'get',
    data: {fingerprint: getUserFingerPrint()},
    success: function (response) {
      $shoppingCartContents.innerHTML = response;
      changeCounter(0);
    },
    error: function (xhr) {
      console.log('Failed to empty cart');
    }
  });
}

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

// <!------------------------- Main Search View ------------------------->
var $searchBtn = $('#search-btn');
var isLoading = 'is-loading';

$searchBtn.click(function () {
  $searchBtn.addClass(isLoading);
});

// <!------------------------- Save Games To 'Cart' ------------------------->
var addToCartBtns = document.querySelectorAll('.add-to-cart-btn');

for (var i = 0; i < addToCartBtns.length; i++) {

  addToCartBtns[i].addEventListener('click', function (e) {

    var identifierClass = e.srcElement.id.toString();
    var $gameName = $('strong#game-name.' + identifierClass).get(0).innerText;

    $.ajax({
      url: '/cart/add',
      type: 'get',
      data: {
        fingerprint: getUserFingerPrint(),
        name: $gameName
      },
      success: function (response) {
        changeCounter(response);
        populateCartItems();
      },
      error: function (xhr) {
        console.log('Failed to add to cart');
      }
    });

  });

}

/* jshint ignore:end */